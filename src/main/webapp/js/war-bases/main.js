"use strict";

$(document).ready(function() {
    var LOAD_BATCH_SIZE = 3;
    var toLoad = players.slice(0);
    var loading = [];
    var results = [];

    var townHallContainerTemplate = $("#town-hall-container-template").html();
    Mustache.parse(townHallContainerTemplate, ['[[', ']]']);
    var resultsContainer = $("#results");
    var problemsContainer = $("#problems");
    var loadingContainer = $("#loading");

    var addPermanentError = function(player, message) {
        results.push({ player: player, error: message });
        loading = _.reject(loading, _.matcher(player));
        loadNext();
    };

    var addTemporaryError = function(player, message, numAttempts) {
        results.push({ player: player, error: message + ", trying again shortly", numAttempts: numAttempts });
        loading = _.reject(loading, _.matcher(player));
        toLoad.push(player);
        loadNext();
    };

    var addResult = function(player, report) {
        results.push({ player: player, report: report });
        loading = _.reject(loading, _.matcher(player));
        loadNext();
    };

    var loadPlayer = function(player, attemptNum) {
        var timeout = Math.pow(attemptNum - 1, 1.5) * 1000;
        if (timeout > 0) {
            console.log("Loading", player.ign, "with timeout " + timeout);
        }
        setTimeout(
            function() {
                loading.push(next);
                jQuery.getJSON(player.analysisSummaryUrl)
                    .done(function (report) {
                        addResult(player, report);
                    })
                    .fail(function (response) {
                        if (response.status == 404 || response.status == 400) {
                            addPermanentError(player, response.responseJSON);
                            return;
                        }

                        if (response.status == 503) {
                            addTemporaryError(player, "Game Servers connection not available", attemptNum);
                            return;
                        }

                        addTemporaryError(player, 'Unknown error encountered', attemptNum);
                    });
            },
            timeout
        );
    };

    var loadNext = function() {
        while (toLoad.length > 0 && loading.length < LOAD_BATCH_SIZE) {
            var next = _.head(toLoad);
            toLoad = _.tail(toLoad);

            var attemptNum = 1;
            var report = _.find(results, function(result) { return result.player == next; });
            if (report != null && report.numAttempts) {
                attemptNum = report.numAttempts + 1;
            }
            loadPlayer(next, attemptNum);
        }

        render();
    };

    var renderProblem = function(result) {
        problemsContainer.removeClass("hidden").show();
        var problemId = "problem-" + result.player.id;
        if (problemsContainer.find("#" + problemId).size() == 0) {
            problemsContainer.append(
                $("<div />").attr("id", problemId)
                    .html(result.player.ign + ": " + result.error)
            );
        }
    };

    var ensureTownHallContainerRendered = function(result) {
        var townHallContainerId = "town-hall-table-" + result.report.townHallLevel;
        var townHallContainer = $("#" + townHallContainerId);
        if (townHallContainer.size() == 0) {
            townHallContainer = $(Mustache.render(townHallContainerTemplate, {
                "containerId": townHallContainerId,
                "level": result.report.townHallLevel,
                "rules": _.pluck(result.report.resultSummaries, 'shortName')
            }));
            resultsContainer.append(townHallContainer);

            new Clipboard(townHallContainer.find("[data-clipboard-target]").get(0));
        }
        return townHallContainer;
    };

    var createResultRow = function(rowId, result, anyError) {
        var ruleOrder = _.map(
            resultsContainer.find("table thead th.result-col"),
            function(col) { return $(col).data("rule"); }
        );

        return $("<tr />").attr("id", rowId)
            .addClass(anyError ? 'danger' : '')
            .append($("<td />").append(result.player.ign))
            .append($("<td />").append($("<a />").attr("href", result.player.analysisUrl).attr("target", "_blank").html(result.player.analysisUrl)))
            .append(
                _.map(
                    _.sortBy(result.report.resultSummaries, function(summary) { return ruleOrder.indexOf(summary.shortName); }),
                    function(summary) {
                        return $("<td />").append(summary.success ? '' : $('<span />').addClass('glyphicon glyphicon-remove-sign'));
                    }
                )
            )
            .append($("<td />").append(" " + result.report.connectionTime + " | " + result.report.analysisTime));
    };

    var render = function() {
        // Loading
        if (loading.length == 0) {
            loadingContainer.hide();
        } else {
            loadingContainer.show();
            loadingContainer.find(".loading-names").html(_.pluck(loading, 'ign').join(", "));
            if (toLoad.length > 0) {
                loadingContainer.find(".queued-button").show();
                loadingContainer.find(".queued-count").html(toLoad.length);
                loadingContainer.find(".queued-names").html(_.pluck(toLoad, 'ign').join(', '));
            } else {
                loadingContainer.find(".queued-button").hide();
            }
        }

        // Table Results
        _.each(
            results,
            function(result) {
                var rowId = "player-row-" + result.player.id;
                if ($("#" + rowId).size() > 0) {
                    return;
                }

                if (result.report == null) {
                    renderProblem(result);
                    return;
                }
                
                var townHallContainer = ensureTownHallContainerRendered(result);

                var anyError = _.some(result.report.resultSummaries, function(summary) { return !summary.success; });
                createResultRow(rowId, result, anyError)
                    .appendTo(townHallContainer.find("tbody"))
                    .slideDown();

                if (anyError) {
                    townHallContainer.find(".plain-text-summary")
                        .find("textarea")
                        .append(result.player.ign + ": " + _.pluck(result.report.resultSummaries, 'shortName').join(', ') + "\n");
                }
            }
        );
    };

    loadNext();
});