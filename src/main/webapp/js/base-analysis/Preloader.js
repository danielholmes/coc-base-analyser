"use strict";

var Preloader = function() {
    return {
        loadAssets: function(callback) {
            var queue = new createjs.LoadQueue();
            
            var handleAssetsLoadComplete = function() {
                callback(queue);
            };

            queue.on("complete", handleAssetsLoadComplete, this);
            queue.loadManifest([
                { id: "redMoonBuildings", src:"/images/buildings-sprite.png" },
                { id: "redMoonWalls", src:"/images/walls.png" }
            ]);
        }
    };
};