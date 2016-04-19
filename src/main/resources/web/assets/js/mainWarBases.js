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

                            var headRow = $('<tr />').append($('<th />').append('Name'));
                            if (rules.length > 0) {
                                headRow.append($('<th />').append('Report'))
                                    .append(_.map(rules, function (rule) {
                                        return $('<th />').append(rule);
                                    }));
                            }
                            return $('<div />')
                                .append($('<h4 />').html("Town Hall " + pair[0]))
                                .append(
                                    $('<table />')
                                        .addClass("table")
                                        .addClass("table-bordered")
                                        .append($('<thead />').append(headRow))
                                        .append(
                                            _.map(
                                                pair[1],
                                                function(report) {
                                                    var link = "/#" + encodeURI(report.userName) + "/war";
                                                    if (report['resultSummaries']) {
                                                        var allSuccess = _.all(report['resultSummaries'], function(summary) { return summary['success']; });
                                                        return $('<tr />')
                                                            .addClass(allSuccess ? 'success' : 'fail')
                                                            .append($('<td />').append(report.userName))
                                                            .append($('<td />').append($('<a />').attr("href", link).attr("target", "_blank").append(link)))
                                                            .append(
                                                                _.map(rules, function(rule) {
                                                                    var summary = _.find(report['resultSummaries'], function(s) { return s.name == rule; });
                                                                    return $('<td />')
                                                                        .append(
                                                                            $('<span />').addClass('glyphicon')
                                                                                .addClass(summary.success ? '' : 'glyphicon-remove-sign')
                                                                        );
                                                                })
                                                            );
                                                    }

                                                    return $('<tr />')
                                                        .addClass('success')
                                                        .append($('<td />').append(report.userName));
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