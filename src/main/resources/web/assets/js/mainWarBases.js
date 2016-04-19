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
                                        .addClass("table")
                                        .addClass("table-bordered")
                                        .append(
                                            $('<thead />').append(
                                                $('<tr />')
                                                    .append($('<th />').append('Name'))
                                                    .append($('<th />').append('Report'))
                                                    .append(_.map(rules, function(rule) { return $('<th />').append(rule); }))
                                            )
                                        )
                                        .append(
                                            _.map(
                                                pair[1],
                                                function(report) {
                                                    var link = "/#" + encodeURI(report.userName) + "/war";
                                                    var allSuccess = _.all(report['resultSummaries'], function(summary) { return summary['success']; });
                                                    return $('<tr />')
                                                        .addClass(allSuccess ? 'success' : 'fail')
                                                        .append($('<td />').append(report.userName))
                                                        .append($('<td />').append($('<a />').attr("href", link).attr("target", "_blank").append(link)))
                                                        .append(
                                                            _.map(report['resultSummaries'], function(summary) {
                                                                return $('<td />')
                                                                    .append(
                                                                        $('<span />').addClass('glyphicon')
                                                                            .addClass(summary.success ? '' : 'glyphicon-remove-sign')
                                                                    );
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