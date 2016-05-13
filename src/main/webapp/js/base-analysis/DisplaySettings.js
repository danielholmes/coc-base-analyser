"use strict";

var DisplaySettings = function(jStorage) {
    var KEY = "baseDisplaySettings";
    var showGridChanged = new signals.Signal();

    var getStored = function() {
        return _.extend({ showGrid: false }, jStorage.get(KEY, {}));
    };

    var store = function() {
        jStorage.set(KEY, { showGrid: showGrid });
    };

    var setShowGrid = function(newShowGrid) {
        if (newShowGrid != showGrid) {
            showGrid = newShowGrid;
            showGridChanged.dispatch();
            store();
        }
    };
    
    var getShowGrid = function() {
        return showGrid;
    };

    var initStored = getStored();
    var showGrid = initStored.showGrid;
    
    return {
        setShowGrid: setShowGrid,
        getShowGrid: getShowGrid,
        showGridChanged: showGridChanged
    };
};