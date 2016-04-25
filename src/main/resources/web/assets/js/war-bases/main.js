$(document).ready(function() {
    var LOAD_BATCH_SIZE = 6;
    var toLoad = players.slice(0);
    var loading = [];
    var results = [];

    var townHallContainerTemplate = $("#town-hall-container-template").html();
    Mustache.parse(townHallContainerTemplate, ['[[', ']]']);
    var resultsContainer = $("#results");
    var problemsContainer = $("#problems");

    var loadPlayer = function(player) {
        jQuery.getJSON("/village-analysis/" + encodeURI(player.id) + "/war/summary")
            .always(function() {
                loading = _.reject(loading, function(check) { return check == player; });
                load();
            })
            .done(function(report) {
                results.push({
                    player: player,
                    report: report
                });
                render();
            })
            .fail(function(response) {
                if (response.status == 404 || response.status == 400) {
                    results.push({
                        player: player,
                        error: response.responseJSON
                    });
                    render();
                } else {
                    results.push({
                        player: player,
                        error: "Some error encountered, please try again"
                    });
                    render();
                }
            });
    };

    var load = function() {
        while (toLoad.length > 0 && loading.length < LOAD_BATCH_SIZE) {
            var next = _.head(toLoad);
            toLoad = _.tail(toLoad);
            loading.push(next);
            loadPlayer(next);
        }

        render();
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

        // Results
        _.each(
            results,
            function(result) {
                var rowId = "player-row-" + result.player.id;
                if ($("#" + rowId).size() > 0) {
                    return;
                }

                if (result.report == null) {
                    problemsContainer.show();
                    var problemId = "problem-" + result.player.ign;
                    if (problemsContainer.find("#" + problemId).size() == 0) {
                        problemsContainer.append($("<div />").attr("id", problemId).html(result.player.ign + ": " + result.error));
                    }
                    
                    return;
                }
                
                var townHallContainerId = "town-hall-table-" + result.report.townHallLevel;
                var townHallContainer = $("#" + townHallContainerId);
                if (townHallContainer.size() == 0) {
                    townHallContainer = $(Mustache.render(townHallContainerTemplate, {
                        "containerId": townHallContainerId,
                        "level": result.report.townHallLevel,
                        "rules": _.pluck(result.report.resultSummaries, 'name')
                    }));
                    resultsContainer.append(townHallContainer);
                }

                var ruleOrder = _.map(
                    resultsContainer.find("table thead th.result-col"),
                    function(col) { return $(col).data("rule"); }
                );

                var anyError = _.some(result.report.resultSummaries, function(summary) { return !summary.success; });
                var link = "/#" + encodeURI(result.player.ign) + "/war";
                $("<tr />").attr("id", rowId)
                    .addClass(anyError ? 'danger' : '')
                    .append($("<td />").append(result.player.ign))
                    .append($("<td />").append($("<a />").attr("href", link).attr("target", "_blank").html(link)))
                    .append(
                        _.map(
                            _.sortBy(result.report.resultSummaries, function(summary) { return ruleOrder.indexOf(summary.name); }),
                            function(summary) {
                                return $("<td />").append(summary.success ? '' : $('<span />').addClass('glyphicon glyphicon-remove-sign'));
                            }
                        )
                    )
                    .appendTo(townHallContainer.find("tbody"))
                    .slideDown();
            }
        );
    };

    load();
});