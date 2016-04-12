'use strict';

var ui = (function($, model, mapDisplay, window) {
    var runningAnalysis = false;
    var reportValid = false;
    var sizeValid = false;
    var activeRuleValid = false;

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

    var invalidateSize = function() {
        sizeValid = false;
        render();
    };

    var invalidateRule = function() {
        activeRuleValid = false;
        render();
    };

    var renderMapSize = function() {
        if (sizeValid) {
            return;
        }

        var panelGroup = $("#results-panel-group");
        $(mapDisplay.canvas).hide();

        // Don't know why width - padding is the desired width, found by trial and error
        var padding = ($(mapDisplay.canvas).parent().outerWidth() - $(mapDisplay.canvas).parent().width()) / 2;
        var canvasSize;
        if ($(window).width() < 980) {
            // Vertical column
            canvasSize = $(mapDisplay.canvas).parent().width() - padding;
        } else {
            // side by side
            canvasSize = $(window.document).height() - $(mapDisplay.canvas).parent().offset().top - 20;
        }
        mapDisplay.canvas.width = canvasSize;
        mapDisplay.canvas.height = canvasSize;
        $(mapDisplay.canvas).show();

        sizeValid = true;
    };

    var renderReport = function() {
        if (reportValid) {
            return;
        }

        reportValid = true;
        var panelGroup = $("#results-panel-group");
        panelGroup.empty();
        if (!model.hasReport()) {
            $("#report").hide();
            return;
        }

        $("#report").show();
        var panels = _.map(
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
                        title: rule.title,
                        description: rule.description,
                        ruleName: result.name,
                        success: result.success
                    }
                ));
            }
        );
        panelGroup.append(panels);
        panelGroup.find(".panel.failed:first a").trigger("click");
    };

    var renderRule = function() {
        if (activeRuleValid) {
            return;
        }

        activeRuleValid = true;
        var panels = $("#results-panel-group").find(".panel");
        panels.removeClass("active");
        if (model.hasActiveRule()) {
            panels.filter("#panel-" + model.getActiveRuleName()).addClass("active");
        }
    };

    var render = function() {
        renderMapSize();
        renderReport();
        renderRule();
        mapDisplay.render();
    };

    model.reportChanged.add(_.bind(invalidateReport, this));
    model.ruleChanged.add(_.bind(invalidateRule, this));

    var searchButton;

    $(document).ready(function () {
        var searchForm = $("#searchForm");
        searchButton = searchForm.find("button[type='submit']");
        var userNameField = $("#userNameField");
        var USER_NAME_KEY = "userName";
        userNameField.val($.jStorage.get(USER_NAME_KEY, ""));

        $("#results-panel-group").on("show.bs.collapse", function(event) {
            model.setActiveRuleName($(event.target).data("rule-name"));
        });
        $("#results-panel-group").on("hide.bs.collapse", function(event) {
            if (model.getActiveRuleName() == $(event.target).data("rule-name")) {
                model.clearActiveRuleName();
            }
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
                    if (response.status == 404 || response.status == 400) {
                        alert(response.responseJSON);
                        return false;
                    }

                    alert("There was an unknown problem, please try again later");
                })
                .always(function () {
                    stopLoading();
                });

            return false;
        });

        //console.log("TODO: Remove");searchForm.submit();
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

    $(window).on("resize", _.bind(invalidateSize, this));

    return {
        render: render
    }
})(jQuery, model, mapDisplay2d, window);