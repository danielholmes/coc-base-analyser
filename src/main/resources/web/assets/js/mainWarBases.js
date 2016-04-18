$(document).ready(function() {
    var results = $("#results");
    results.html("Loading...");
    
    $.getJSON("/clan-war-bases-analysis/alpha")
        .done(function(response){
            results.empty()
                .append(
                    _.map(
                        _.sortBy(
                            _.pairs(_.groupBy(response.reports, 'townHallLevel')),
                            function(pair) { return pair[0] }
                        ),
                        function(pair) {
                            var rules = _.uniq(_.flatten(
                                _.map(
                                    pair[1],
                                    function(report) {
                                        return _.map(report['resultSummaries'], function(result) { return result['name']; });
                                    }
                                )
                            ));

                            return $('<div />').append($('<h3 />').html(pair[0]))
                                .append(
                                    $('<table />')
                                        .append(
                                            $('<thead />').append(
                                                $('<tr />').append(_.map(rules, function(rule) { return $('<th />').append(rule); }))
                                            )
                                        )
                                        .append(
                                            _.map(
                                                pair[1],
                                                function(report) {
                                                    return $('<tr />')
                                                        .append(
                                                            _.map(report.resultSummaries, function(summary) {
                                                                $('<td />').append(summary.success)
                                                            })
                                                        );
                                                }
                                            )
                                        )
                                );
                        }
                    )
                );
        })
        .fail(function(response) {
            alert("there was some sort of error. Please try again later");
        });
});