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
                { id: "buildings", src:"assets/buildings-sprite.png" },
                { id: "walls", src:"assets/walls.png" }
            ]);
        }
    };
})();