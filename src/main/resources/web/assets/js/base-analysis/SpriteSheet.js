var SpriteSheet = function(image, defs) {
    var nullDisplay = new createjs.Container();
    nullDisplay.sourceRect = new createjs.Rectangle(0, 0, 0, 0);

    var create = function(element, mapConfig) {
        var sheetDef = defs[element.typeName];
        if (sheetDef == null) {
            console.error("Cannot render " + element.typeName);
            return nullDisplay;
        }

        if (element.level > sheetDef.numLevels) {
            console.error("Cannot render " + element.typeName + " at level " + element.level);
            return nullDisplay;
        }

        var widthRatio = element.block.size * mapConfig.tileSize / sheetDef.width;
        var heightRatio = element.block.size * mapConfig.tileSize / sheetDef.height;
        var useScale = Math.min(widthRatio, heightRatio);
        var sheetIndex = element.level - 1;
        var bitmap = new createjs.Bitmap(image);
        bitmap.sourceRect = new createjs.Rectangle(
            sheetDef.x + sheetIndex * sheetDef.width,
            sheetDef.y,
            sheetDef.width,
            sheetDef.height
        );
        bitmap.scaleX = useScale;
        bitmap.scaleY = useScale;
        return bitmap;
    };
    
    return {
        create: create
    };
};