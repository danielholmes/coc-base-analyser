"use strict";

var RedMoonBuildingSpriteSheet = function(image) {
    var defs = {
        "SkeletonTrap": {
            x: 1092,
            y: 304,
            width: 24,
            height: 32,
            gap: 2,
            levelMultiplier: 0.5
        },
        "AirBomb": {
            x: 994,
            y: 297,
            width: 25,
            height: 40,
            gap: 0,
            levelMultiplier: 0.5
        },
        "SeekingAirMine": {
            x: 1044,
            y: 299,
            width: 25,
            height: 39,
            gap: 0,
            levelMultiplier: 0.5
        },
        "SpringTrap": {
            x: 969,
            y: 311,
            width: 25,
            height: 26,
            gap: 0
        },
        "Bomb": {
            x: 893,
            y: 313,
            width: 17,
            height: 23,
            gap: 9,
            levelMultiplier: 0.5
        },
        "GiantBomb": {
            x: 896,
            y: 439,
            width: 37,
            height: 39,
            gap: 12,
            levelMultiplier: 0.5
        },
        "TownHall": {
            x: 0,
            y: 5,
            width: 90,
            height: 90,
            gap: 12
        },
        "Laboratory": {
            x: 0,
            y: 118,
            width: 80,
            height: 64,
            gap: 22
        },
        "BuilderHut": {
            x: 697,
            y: 342,
            width: 36,
            height: 38,
            gap: 0
        },
        "BarbarianKingAltar": {
            x: 749,
            y: 339,
            width: 60,
            height: 43,
            gap: 0
        },
        "ArcherQueenAltar": {
            x: 824,
            y: 338,
            width: 59,
            height: 42,
            gap: 0
        },
        "ArmyCamp": {
            x: 0,
            y: 196,
            width: 121,
            height: 93,
            gap: 8
        },
        "ClanCastle": {
            x: 0,
            y: 308,
            width: 75,
            height: 77,
            gap: 1
        },
        "Barrack": {
            x: 0,
            y: 417,
            width: 69,
            height: 61,
            gap: 7
        },
        "ArcherTower": {
            x: 0,
            y: 507,
            width: 60,
            height: 70,
            gap: 16
        },
        "Cannon": {
            x: 0,
            y: 628,
            width: 60,
            height: 47,
            gap: 16
        },
        "AirDefense": {
            x: 0,
            y: 704,
            width: 60,
            height: 64,
            gap: 16
        },
        "XBow": {
            x: 700,
            y: 715,
            width: 64,
            height: 57,
            gap: 10
        },
        "WizardTower": {
            x: 0,
            y: 1092,
            width: 60,
            height: 63,
            gap: 16
        },
        "DarkElixirCollector": {
            x: 648,
            y: 1094,
            width: 60,
            height: 59,
            gap: 16
        },
        "Mortar": {
            x: 0,
            y: 1209,
            width: 60,
            height: 44,
            gap: 16
        },
        "DarkElixirStorage": {
            x: 645,
            y: 1194,
            width: 65,
            height: 63,
            gap: 11
        },
        "GoldMine": {
            x: 0,
            y: 813,
            width: 60,
            height: 52,
            gap: 16
        },
        "GoldStorage": {
            x: 0,
            y: 899,
            width: 69,
            height: 69,
            gap: 7
        },
        "InfernoTower": {
            x: 893,
            y: 903,
            width: 33,
            height: 60,
            gap: 16
        },
        "DarkBarrack": {
            x: 0,
            y: 995,
            width: 69,
            height: 69,
            gap: 7
        },
        "SpellFactory": {
            x: 550,
            y: 1005,
            width: 68,
            height: 57,
            gap: 7
        },
        "ElixirCollector": {
            x: 0,
            y: 1289,
            width: 62,
            height: 60,
            gap: 14
        },
        "ElixirStorage": {
            x: 0,
            y: 1385,
            width: 66,
            height: 66,
            gap: 10
        },
        "DarkSpellFactory": {
            x: 912,
            y: 1398,
            width: 66,
            height: 49,
            gap: 10
        },
        "HiddenTesla": {
            x: 0,
            y: 1491,
            width: 49,
            height: 54,
            gap: 0
        },
        "AirSweeper": {
            x: 546,
            y: 1503,
            width: 49,
            height: 44,
            gap: 0
        }
    };
    
    var create = function(element, mapDimensions) {
        var sheetDef = defs[element.typeName];
        if (sheetDef == null) {
            console.error("Cannot render " + element.typeName);
            var display = new createjs.Container();
            display.sourceRect = new createjs.Rectangle(0, 0, 0, 0);
            return display;
        }
        var widthRatio = mapDimensions.toCanvasSize(element.block.size) / sheetDef.width;
        var heightRatio = mapDimensions.toCanvasSize(element.block.size) / sheetDef.height;
        var useScale = Math.min(widthRatio, heightRatio);
        var sheetIndex = element.level - 1;
        if (sheetDef.levelMultiplier) {
            sheetIndex = Math.floor((element.level - 1) * sheetDef.levelMultiplier);
        }
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
    };
};