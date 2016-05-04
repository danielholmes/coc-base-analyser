$(document).ready(function() {
    var LOAD_BATCH_SIZE = 4;
    var toLoad = players.slice(0);
    var loading = [];
    var results = [];

    var townHallContainerTemplate = $("#town-hall-container-template").html();
    Mustache.parse(townHallContainerTemplate, ['[[', ']]']);
    var resultsContainer = $("#results");
    var problemsContainer = $("#problems");

    var addResult = function(result) {
        results.push(result);
        render();
    };

    $(document).on('click', '.try-player-again', function(event) {
        var player = $(event.currentTarget).data('player');
        results = _.reject(results, function(result) { return result.player == player; });
        $("#" + getProblemId(player)).remove();
        toLoad.push(player);
        loadNext();
        return false;
    });

    var loadPlayer = function(player) {
        jQuery.getJSON(player.analysisSummaryUrl)
            .always(function() {
                loading = _.reject(loading, function(check) { return check == player; });
                loadNext();
            })
            .done(function(report) {
                addResult({
                    player: player,
                    report: report
                });
            })
            .fail(function(response) {
                if (response.status == 404 || response.status == 400) {
                    addResult({
                        player: player,
                        error: response.responseJSON
                    });
                    return;
                }

                if (response.status == 503) {
                    addResult({
                        player: player,
                        error: "Game Servers connection not available, try again later"
                    });
                    return;
                }

                addResult({
                    player: player,
                    error: 'Unknown error encountered'
                });
            });
    };

    var loadNext = function() {
        while (toLoad.length > 0 && loading.length < LOAD_BATCH_SIZE) {
            var next = _.head(toLoad);
            toLoad = _.tail(toLoad);
            loading.push(next);
            loadPlayer(next);
        }

        render();
    };

    var getProblemId = function(player) {
        return "problem-" + player.id;
    };

    var render = function() {
        // Loading
        if (loading.length == 0) {
            $("#loading").empty();
        } else {
            var message = "Analysing: " + _.pluck(loading, 'ign').join(", ");
            if (toLoad.length > 0) {
                message += " + " + toLoad.length + " more";
            }
            $("#loading").html(message);
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
                    problemsContainer.removeClass("hidden").show();
                    var problemId = getProblemId(result.player);
                    if (problemsContainer.find("#" + problemId).size() == 0) {
                        problemsContainer.append(
                            $("<div />").attr("id", problemId)
                                .html(result.player.ign + ": " + result.error + " ")
                                .append($('<a />').data('player', result.player).addClass('try-player-again btn btn-default btn-xs').html('try again'))
                        );
                    }
                    
                    return;
                }
                
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

                var ruleOrder = _.map(
                    resultsContainer.find("table thead th.result-col"),
                    function(col) { return $(col).data("rule"); }
                );

                var anyError = _.some(result.report.resultSummaries, function(summary) { return !summary.success; });
                $("<tr />").attr("id", rowId)
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