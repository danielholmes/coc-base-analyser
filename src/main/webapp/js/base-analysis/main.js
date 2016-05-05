"use strict";

$(document).ready(function() {
    preloader.loadAssets(function(queue) {
        mapDisplay2d.setAssets({
            "redMoonBuildings": queue.getResult("redMoonBuildings"),
            "redMoonWalls": queue.getResult("redMoonWalls"),
            //"spriteSheetImage": queue.getResult("spriteSheetImage"),
            //"spriteSheetDefs": queue.getResult("spriteSheetDefs")
        });
        ui.render();
    });

    model.setReport(report);
    console.log("Analysis")
});