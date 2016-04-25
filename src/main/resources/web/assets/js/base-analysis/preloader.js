'use strict';

var preloader = (function() {
    return {
        loadAssets: function(callback) {
            var queue = new createjs.LoadQueue();
            
            var handleAssetsLoadComplete = function() {
                callback(queue);
            };

            queue.on("complete", handleAssetsLoadComplete, this);
            queue.loadManifest([
                { id: "redMoonBuildings", src:"assets/buildings-sprite.png" },
                { id: "redMoonWalls", src:"assets/walls.png" },
                //{ id: "spriteSheetImage", src:"assets/sprite-sheet.png" },
                //{ id: "spriteSheetDefs", src:"assets/sprite-sheet.json" }
            ]);
        }
    };
})();