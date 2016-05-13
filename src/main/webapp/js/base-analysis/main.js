"use strict";

$(document).ready(function() {
    // Globals mapConfig, report, document, window

    var model = new Model();

    var displaySettings = new DisplaySettings(jQuery.jStorage);
    var display = new MapDisplay2d(document.getElementById("villageImage"), mapConfig, model, displaySettings);
    var ui = new Ui(jQuery, model, display, window, displaySettings);

    var preloader = new Preloader();
    preloader.loadAssets(function(queue) {
        display.setAssets({
            "redMoonBuildings": queue.getResult("redMoonBuildings"),
            "redMoonWalls": queue.getResult("redMoonWalls")
        });
        ui.render();
    });

    model.setReport(report);
});