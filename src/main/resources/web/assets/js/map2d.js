'use strict';

var map = (function(document) {
    var canvas = document.getElementById("villageImage");
    var stage = new createjs.Stage(canvas);
    stage.autoClear = false;
    var bgContainer = new createjs.Container();
    stage.addChild(bgContainer);
    var buildingsContainer = new createjs.Container();
    stage.addChild(buildingsContainer);
    var extrasContainer = new createjs.Container();
    stage.addChild(extrasContainer);

    var assets = null;

    var buildingsSheet = {
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
        "BarbarianKing": {
            x: 749,
            y: 339,
            width: 60,
            height: 43,
            gap: 0
        },
        "ArcherQueen": {
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
        "TeslaTower": {
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

    var randomColour = function(seed) {
        var numberSeed = (seed.length * 5.7) % 30;
        return '#' + Math.floor(numberSeed / 30 * 16777215).toString(16);
    }
    
    var setAssets = function(newAssets) {
        assets = newAssets;
        render();
    };

    var render = function() {
        stage.clear();
        bgContainer.removeAllChildren();
        extrasContainer.removeAllChildren();
        buildingsContainer.removeAllChildren();

        if (assets == null) {
            var text = new createjs.Text("Loading Assets", "20px Arial", "#000000");
            text.y = 30;
            extrasContainer.addChild(text);
            stage.update();
            return;
        }

        //renderSpriteSheetDebug();
        if (!model.hasReport()) {
            $("#village").hide();
            stage.update();
            return;
        }

        $("#village").show();
        render2d();

        stage.update();
    };

    function renderSpriteSheetDebug() {
        var canvasContext = canvas.getContext("2d");
        var useScale = Math.min(
            canvas.width / assets.buildings.width,
            canvas.height / assets.buildings.height
        );
        canvasContext.drawImage(
            assets.buildings,
            0,
            0,
            assets.buildings.width,
            assets.buildings.height,
            0,
            0,
            assets.buildings.width * useScale,
            assets.buildings.height * useScale
        );

        for (var i in buildingsSheet) {
            var sheetDef = buildingsSheet[i];
            canvasContext.strokeStyle = randomColour(i);
            // First gap
            canvasContext.strokeRect(
                sheetDef.x * useScale,
                sheetDef.y * useScale,
                sheetDef.gap / 2 * useScale,
                sheetDef.height * useScale
            );
            for (var j = 0; j < 13; j++) {
                var startX = sheetDef.x + j * (sheetDef.gap + sheetDef.width) + sheetDef.gap / 2;
                // image
                canvasContext.strokeRect(
                    startX * useScale,
                    sheetDef.y * useScale,
                    sheetDef.width * useScale,
                    sheetDef.height * useScale
                );
                // Gap
                canvasContext.strokeRect(
                    (startX + sheetDef.width) * useScale,
                    sheetDef.y * useScale,
                    sheetDef.gap * useScale,
                    sheetDef.height * useScale
                );
            }
        }
    }

    function render2d() {
        // TODO: Get map/coordinate system config from api/backend
        var mapTiles = 44;
        var borderTiles = 1;
        var mapConfig = {
            mapTiles: mapTiles,
            borderTiles: borderTiles,
            tileSize: Math.min(canvas.width, canvas.height) / (mapTiles + 2 * borderTiles)
        };
        render2dGrass(mapConfig);
        //render2dRandomSolidColourElements(mapConfig);
        render2dPreventTroopDrops(mapConfig);
        render2dImageBuildings(mapConfig);
        renderActiveRule(mapConfig);
    }

    function renderActiveRule(mapConfig) {
        if (!model.hasActiveRule()) {
            return;
        }

        var result = _.find(model.getReport().results, function (test) {
            return test.name == model.getActiveRuleName();
        });
        switch (result.name) {
            case 'ArcherAnchor':
                renderArcherAnchor(result, mapConfig);
                break;
            case 'HogCCLure':
                renderHogCCLure(result, mapConfig);
                break;
            case 'HighHPUnderAirDef':
                renderHighHPUnderAirDef(result, mapConfig);
                break;
            default:
                console.error('Don\'t know how to render active rule: ' + result.name);
        }
    }

    function renderArcherAnchor(result, mapConfig) {
        _.each(
            _.map(
                result.targetings,
                function (targeting) {
                    var line = new createjs.Shape();
                    line.graphics
                        .beginStroke("#ff0000")
                        .moveTo(0, 0)
                        .lineTo(
                            (targeting.hitPoint.x - targeting.standingPosition.x) * mapConfig.tileSize,
                            (targeting.hitPoint.y - targeting.standingPosition.y) * mapConfig.tileSize
                        );
                    line.x = targeting.standingPosition.x * mapConfig.tileSize;
                    line.y = targeting.standingPosition.y * mapConfig.tileSize;
                    extrasContainer.addChild(line);
                    return _.find(
                        buildingsContainer.children,
                        function (buildingContainer) {
                            return buildingContainer.id == targeting.targetingId;
                        }
                    );
                }
            ),
            function (buildingContainer) {
                buildingContainer.filters = [
                    new createjs.ColorFilter(
                        1, 0, 0, 1,
                        0, 0, 0, 0
                    )
                ];
                buildingContainer.cache(0, 0, 1000, 1000);
            }
        );
        // TODO: Ranges of all ground targeting
        //renderElementRanges(mapConfig, "ClanCastle");
    }

    function renderHogCCLure(result, mapConfig) {
        _.each(
            _.map(
                result.targetings,
                function (targeting) {
                    var line = new createjs.Shape();
                    line.graphics
                        .beginStroke("#ff0000")
                        .moveTo(0, 0)
                        .lineTo(
                            (targeting.hitPoint.x - targeting.startPosition.x) * mapConfig.tileSize,
                            (targeting.hitPoint.y - targeting.startPosition.y) * mapConfig.tileSize
                        );
                    line.x = targeting.startPosition.x * mapConfig.tileSize;
                    line.y = targeting.startPosition.y * mapConfig.tileSize;
                    extrasContainer.addChild(line);
                    return _.find(
                        buildingsContainer.children,
                        function (buildingContainer) {
                            return buildingContainer.id == targeting.targetingId;
                        }
                    );
                }
            ),
            function (buildingContainer) {
                buildingContainer.filters = [
                    new createjs.ColorFilter(
                        1, 0, 0, 1,
                        0, 0, 0, 0
                    )
                ];
                buildingContainer.cache(0, 0, 1000, 1000);
            }
        );
        renderElementRanges(mapConfig, "ClanCastle");
    }

    function renderHighHPUnderAirDef(result, mapConfig) {
        // TODO: Highlight in green all high hp buildings
        _.each(
            _.map(
                result.outOfAirDefRange,
                function (id) {
                    return _.find(
                        buildingsContainer.children,
                        function (buildingContainer) {
                            return buildingContainer.id == id;
                        }
                    );
                }
            ),
            function (buildingContainer) {
                buildingContainer.filters = [
                    new createjs.ColorFilter(
                        1, 0, 0, 1,
                        0, 0, 0, 0
                    )
                ];
                buildingContainer.cache(0, 0, 1000, 1000);
            }
        );
        renderElementRanges(mapConfig, "AirDefense");
    }

    function render2dGrass(mapConfig) {
        var grass = new createjs.Shape();
        grass.graphics
            .beginFill("#8cbf15")
            .drawRect(
                0,
                0,
                mapConfig.mapTiles * mapConfig.tileSize,
                mapConfig.mapTiles * mapConfig.tileSize
            );
        grass.x = mapConfig.borderTiles * mapConfig.tileSize;
        grass.y = mapConfig.borderTiles * mapConfig.tileSize;
        bgContainer.addChild(grass);
    }

    function render2dPreventTroopDrops(mapConfig) {
        var allPrevents = new createjs.Container();
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            if (element.preventTroopDropBlock.size == 0) {
                continue;
            }
            var prevent = new createjs.Shape();
            prevent.graphics
                .beginFill("rgba(255,255,255,1)")
                .drawRect(
                    0,
                    0,
                    element.preventTroopDropBlock.size * mapConfig.tileSize,
                    element.preventTroopDropBlock.size * mapConfig.tileSize
                );
            prevent.x = element.preventTroopDropBlock.x * mapConfig.tileSize;
            prevent.y = element.preventTroopDropBlock.y * mapConfig.tileSize;
            allPrevents.addChild(prevent);
        }
        allPrevents.filters = [
            new createjs.ColorFilter(
                1, 1, 1, 0.1,
                0, 0, 0, 0
            )
        ];
        allPrevents.cache(0, 0, 1000, 1000);
        bgContainer.addChild(allPrevents);
    }

    function render2dImageBuildings(mapConfig) {
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            if (element.typeName == "Wall") {
                renderWallImage(element, mapConfig);
                continue;
            }

            renderBuildingImage(element, mapConfig);
        }
    }

    function renderWallImage(element, mapConfig) {
        renderElementImage(
            element,
            mapConfig,
            assets.walls,
            {
                x: 80,
                y: 10 + (element.level - 1) * 52,
                width: 14,
                height: 26,
                gap: 0
            },
            0
        )
    }

    function renderBuildingImage(element, mapConfig) {
        var sheetDef = buildingsSheet[element.typeName];
        if (sheetDef == null) {
            console.error("Don't know how to render: " + element.typeName);
            return;
        }

        renderElementImage(
            element,
            mapConfig,
            assets.buildings,
            sheetDef,
            element.level - 1,
            true
        )
    }

    function renderElementImage(element, mapConfig, image, sheetDef, sheetIndex, drawGrass) {
        var elementContainer = new createjs.Container();
        elementContainer.x = element.block.x * mapConfig.tileSize;
        elementContainer.y = element.block.y * mapConfig.tileSize;
        elementContainer.id = element.id;

        if (drawGrass) {
            var grass = new createjs.Shape();
            grass.graphics
                .beginFill("#6fa414")
                .drawRect(
                    0,
                    0,
                    element.block.size * mapConfig.tileSize,
                    element.block.size * mapConfig.tileSize
                );
            elementContainer.addChild(grass);
        }

        var widthRatio = element.block.size * mapConfig.tileSize / sheetDef.width;
        var heightRatio = element.block.size * mapConfig.tileSize / sheetDef.height;
        var useScale = Math.min(
            widthRatio,
            heightRatio
        );
        var finalWidth = sheetDef.width * useScale;
        var finalHeight = sheetDef.height * useScale;
        var bitmap = new createjs.Bitmap(image);
        bitmap.sourceRect = new createjs.Rectangle(
            sheetDef.x + sheetIndex * (sheetDef.width + sheetDef.gap) + sheetDef.gap / 2,
            sheetDef.y,
            sheetDef.width,
            sheetDef.height
        );
        bitmap.x = ((element.block.size * mapConfig.tileSize) - finalWidth) / 2;
        bitmap.y = ((element.block.size * mapConfig.tileSize) - finalHeight) / 2;
        bitmap.scaleX = useScale;
        bitmap.scaleY = useScale;
        elementContainer.addChild(bitmap);

        buildingsContainer.addChild(elementContainer);
    }

    function render2dRandomSolidColourElements(mapConfig) {
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            canvasContext.fillStyle = randomColour(element.typeName);
            canvasContext.fillRect(
                element.block.x * mapConfig.tileSize,
                element.block.y * mapConfig.tileSize,
                element.block.size * mapConfig.tileSize,
                element.block.size * mapConfig.tileSize
            );
        }
    }

    function renderElementRanges(mapConfig, typeName) {
        _.each(
            _.map(
                _.filter(model.getReport().village.elements, function (element) {
                    return element.typeName == typeName;
                }),
                function (elementToDraw) {
                    var allInfo = new createjs.Container();
                    var circle = new createjs.Shape();
                    circle.graphics
                        .beginStroke("#ffffff")
                        .beginFill("rgba(255,255,255,0.1)")
                        .drawCircle(
                            0,
                            0,
                            elementToDraw.range.outer * mapConfig.tileSize
                        );
                    allInfo.addChild(circle);

                    var lineSize = 1;
                    var vert = new createjs.Shape();
                    vert.graphics
                        .beginStroke("#ffffff")
                        .moveTo(0, -lineSize * mapConfig.tileSize / 2)
                        .lineTo(0, lineSize * mapConfig.tileSize / 2);
                    allInfo.addChild(vert);
                    var hor = new createjs.Shape();
                    hor.graphics
                        .beginStroke("#ffffff")
                        .moveTo(-lineSize * mapConfig.tileSize / 2, 0)
                        .lineTo(lineSize * mapConfig.tileSize / 2, 0);
                    allInfo.addChild(hor);

                    allInfo.x = (elementToDraw.block.x + elementToDraw.block.size / 2) * mapConfig.tileSize;
                    allInfo.y = (elementToDraw.block.y + elementToDraw.block.size / 2) * mapConfig.tileSize;
                    return allInfo;
                }
            ),
            function (circle) {
                extrasContainer.addChild(circle);
            }
        );
    }
    
    return {
        render: render,
        setAssets: setAssets
    };
})(document);