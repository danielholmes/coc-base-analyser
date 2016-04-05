"use strict";

$(document).ready(function() {
    preloader.loadAssets(function(queue) {
        map.setAssets({
            "buildings": queue.getResult("buildings"),
            "walls": queue.getResult("walls")
        });
        ui.render();
    });
    
    ui.render();
});