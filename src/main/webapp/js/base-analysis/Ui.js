"use strict";

var Ui = function($, model, mapDisplay, window, displaySettings) {
    var reportValid = false;
    var sizeValid = false;
    var activeRuleValid = false;
    var panelGroup = $("#results-panel-group");

    var renderTemplate = function (selector, vars) {
        var template = $(selector).html();
        Mustache.parse(template, ['[[', ']]']);
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
        if (Math.abs(panelGroup.offset().top - $(mapDisplay.canvas).parent().offset.top) < 50) {
            // Vertical column
            canvasSize = $(mapDisplay.canvas).parent().width() - padding;
        } else {
            // side by side
            var proposedBasedOnHeight = $(window.document).height() - $(mapDisplay.canvas).parent().offset().top - 20;
            var proposedBasedOnWidth = $(window.document).width() - $(mapDisplay.canvas).parent().offset().left - 40;
            canvasSize = Math.min(proposedBasedOnWidth, proposedBasedOnHeight);
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
        panelGroup.empty();
        if (!model.hasReport()) {
            $("#report").hide();
            return;
        }

        $("#report").show();
        var panels = _.map(
            model.getReport().results,
            function (result) {
                return $(renderTemplate(
                    "#result-panel",
                    {
                        id: result.code,
                        title: result.title,
                        description: result.description,
                        ruleCode: result.code,
                        success: result.success
                    }
                ));
            }
        );
        panelGroup.append(panels);
    };

    var renderRule = function() {
        if (activeRuleValid) {
            return;
        }

        activeRuleValid = true;
        var panels = $("#results-panel-group").find(".panel");
        panels.collapse('hide');
        if (model.hasActiveRule()) {
            panels.filter("#panel-" + model.getActiveRuleCode())
                .find("[role=tabpanel]")
                .collapse('show');
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

    $(document).ready(function () {
        $("#results-panel-group")
            .on("show.bs.collapse", function(event) {
                model.setActiveRuleByCode($(event.target).data("rule-code"));
            })
            .on("hide.bs.collapse", function(event) {
                if (model.getActiveRuleCode() == $(event.target).data("rule-code")) {
                    model.clearActiveRule();
                }
            });

        var showGrid = $("form#display-settings [name=grid]");
        showGrid.prop("checked", displaySettings.getShowGrid());
        showGrid.on("change", function(event) {
           displaySettings.setShowGrid($(event.currentTarget).is(":checked")); 
        });
    });

    $(window).on("resize", _.bind(invalidateSize, this));

    return {
        render: render
    }
};