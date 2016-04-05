'use strict';

var ui = (function(model, map) {
    var runningAnalysis = false;
    var reportValid = true;

    var rules = {
        ArcherAnchor: {
            title: "Archer Anchor",
            description: "There should be no unprotected archer anchors"
        },
        HogCCLure: {
            title: "Easy CC Lure",
            description: "There should be no spaces that allow a hog or giant to lure without first having to destroy a defense"
        },
        HighHPUnderAirDef: {
            title: "High HP covered by Air Defenses",
            description: "All high HP buildings should be within range of your air defenses"
        }
    };

    var renderTemplate = function (selector, vars) {
        var template = $(selector).html();
        Mustache.parse(template);
        return Mustache.render(template, vars);
    };

    var invalidateReport = function() {
        reportValid = false;
        render();
    };

    var render = function() {
        map.render();

        if (reportValid) {
            return;
        }

        reportValid = true;
        var panelGroup = $("#results-panel-group");
        panelGroup.empty();
        if (!model.hasReport()) {
            $("#results").hide();
            return;
        }

        $("#results").show();
        _.each(
            _.map(
                model.getReport().results,
                function (result) {
                    var rule = rules[result.name];
                    if (rule == null) {
                        console.error("Can't represent " + result.name);
                        return $("<div></div>");
                    }

                    return $(renderTemplate(
                        "#result-panel",
                        {
                            id: result.name,
                            title: rule.title + " - " + (result.success ? "Passed" : "Failed"),
                            description: rule.description,
                            ruleName: result.name
                        }
                    ));
                }
            ),
            function (panel) {
                panelGroup.append(panel);
            }
        );
    };

    model.reportChanged.add(_.bind(invalidateReport, this));
    model.ruleChanged.add(_.bind(render, this));

    var searchButton;

    $(document).ready(function () {

        var searchForm = $("#searchForm");
        searchButton = searchForm.find("button[type='submit']");
        var userNameField = $("#userNameField");
        var USER_NAME_KEY = "userName";
        userNameField.val($.jStorage.get(USER_NAME_KEY, ""));

        $("#results-panel-group").on("shown.bs.collapse", function(event) {
            model.setActiveRuleName($(event.target).data("rule-name"));
            render();
        });
        $("#results-panel-group").on("hide.bs.collapse", function () {
            model.clearActiveRuleName();
            render();
        });
        searchForm.on("submit", function () {
            if (runningAnalysis) {
                return false;
            }

            startLoading();
            model.clearReport();

            var userName = userNameField.val();
            $.getJSON("/village-analysis/" + encodeURI(userName))
                .done(function (response) {
                    // TODO: Maybe make jstorage part of model, or its own preferences module
                    $.jStorage.set(USER_NAME_KEY, userName);
                    model.setReport(response);
                })
                .fail(function (response) {
                    if (response.status == 404) {
                        alert("user not found in approved clans");
                        return false;
                    }
                })
                .always(function () {
                    stopLoading();
                });

            return false;
        });
    });

    // Analysis Progress
    function startLoading() {
        if (runningAnalysis) {
            return;
        }
        searchButton.attr("disabled", "disabled")
            .html("Analysing...");
        runningAnalysis = true;
    }

    function stopLoading() {
        if (!runningAnalysis) {
            return;
        }
        searchButton.removeAttr("disabled")
            .html("Run Analysis");
        runningAnalysis = false;
    }

    return {
        render: render
    }
})(model, map);