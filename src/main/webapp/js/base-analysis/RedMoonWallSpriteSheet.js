"use strict";

var RedMoonWallSpriteSheet = function(image) {
    // TODO: Nearly repeated in building sprite sheet, sheetIndex only difference
    var create = function(element, mapDimensions) {
        var sheetDef = {
            x: 80,
            y: 10 + (element.level - 1) * 52,
            width: 14,
            height: 26,
            gap: 0
        };

        var widthRatio = mapDimensions.toCanvasSize(element.block.size) / sheetDef.width;
        var heightRatio = mapDimensions.toCanvasSize(element.block.size) / sheetDef.height;
        var useScale = Math.min(widthRatio, heightRatio);
        var sheetIndex = 0;

        var bitmap = new createjs.Bitmap(image);
        bitmap.sourceRect = new createjs.Rectangle(
            sheetDef.x + sheetIndex * (sheetDef.width + sheetDef.gap) + sheetDef.gap / 2,
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
    }
};