'use strict';

var mapDisplay2d = (function(document) {
    var canvas = document.getElementById("villageImage");
    var stage = new createjs.Stage(canvas);
    stage.autoClear = false;
    var bgContainer = new createjs.Container();
    stage.addChild(bgContainer);
    var buildingsContainer = new createjs.Container();
    stage.addChild(buildingsContainer);
    var extrasContainer = new createjs.Container();
    stage.addChild(extrasContainer);

    var buildingSheet = null;
    var wallSheet = null;

    var randomColour = function(seed) {
        var numberSeed = (seed.length * 5.7) % 30;
        return '#' + Math.floor(numberSeed / 30 * 16777215).toString(16);
    };

    var renderElementRanges = function(mapConfig, elements) {
        _.each(
            _.map(
                elements,
                function (elementToDraw) {
                    var allInfo = new createjs.Container();
                    var circle = new createjs.Shape();
                    circle.graphics
                        .beginStroke("#ffffff")
                        .beginFill("rgba(255,255,255,0.05)")
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
    };

    var renderElementRangesByIds = function(mapConfig, ids) {
        renderElementRanges(mapConfig, model.getVillageElementsByIds(ids));
    };

    var renderElementRangesByTypeName = function(mapConfig, typeName) {
        renderElementRanges(
            mapConfig,
            model.getVillageElementsByTypeName(typeName)
        );
    };

    var renderActiveRule = function(mapConfig) {
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
            case 'AirSnipedDefense':
                renderAirSnipedDefense(result, mapConfig);
                break;
            case 'MinimumCompartments':
                renderMinimumCompartments(result, mapConfig);
                break;
            case 'BKSwappable':
                renderBKSwappable(result, mapConfig);
                break;
            default:
                console.error('Don\'t know how to render active rule: ' + result.name);
        }
    };

    var applyColour = function(display, r, g, b) {
        display.filters = [
            new createjs.ColorFilter(
                r, g, b, 1,
                0, 0, 0, 0
            )
        ];
        display.cache(0, 0, 1000, 1000);
    };

    var renderBKSwappable = function(result, mapConfig) {
        var bk = model.getVillageElementByTypeName("BarbarianKing");
        if (bk == null) {
            return;
        }

        var bkRadiusFill = new createjs.Shape();
        bkRadiusFill.graphics
            .beginFill("#ff0000")
            .drawCircle(
                0,
                0,
                bk.range.outer * mapConfig.tileSize
            );
        bkRadiusFill.x = (bk.block.x + bk.block.size / 2) * mapConfig.tileSize;
        bkRadiusFill.y = (bk.block.y + bk.block.size / 2) * mapConfig.tileSize;
        bkRadiusFill.alpha = 0.6;
        extrasContainer.addChild(bkRadiusFill);

        var exposedMask = new createjs.Shape();
        exposedMask.graphics.beginFill("#00ff00");
        _.each(result.exposedTiles, function(tile) {
            exposedMask.graphics.drawRect(
                tile.x * mapConfig.tileSize,
                tile.y * mapConfig.tileSize,
                mapConfig.tileSize,
                mapConfig.tileSize
            );
        });
        bkRadiusFill.mask = exposedMask;

        renderElementRangesByTypeName(mapConfig, "BarbarianKing");
    };

    var renderMinimumCompartments = function(result, mapConfig) {
        _.each(
            model.getVillageCompartmentsByIds(result.compartments),
            function(compartment) {
                var colour = randomColour(compartment.walls.join("|"));

                var innerDisplay = new createjs.Shape();
                innerDisplay.graphics.beginFill(colour);
                _.each(compartment.innerTiles, function(innerTile) {
                    innerDisplay.graphics.drawRect(
                        innerTile.x * mapConfig.tileSize,
                        innerTile.y * mapConfig.tileSize,
                        mapConfig.tileSize,
                        mapConfig.tileSize
                    );
                });
                innerDisplay.alpha = 0.85;
                extrasContainer.addChild(innerDisplay);

                var wallDisplay = new createjs.Shape();
                wallDisplay.graphics.beginFill(colour);
                _.each(compartment.walls, function(wallId) {
                    var element = model.getVillageElementById(wallId);
                    wallDisplay.graphics.drawRect(
                        element.block.x * mapConfig.tileSize,
                        element.block.y * mapConfig.tileSize,
                        mapConfig.tileSize,
                        mapConfig.tileSize
                    );
                });
                wallDisplay.alpha = 0.5;
                extrasContainer.addChild(wallDisplay);
            }
        );
    };

    var renderAirSnipedDefense = function(result, mapConfig) {
        eachBuildingDisplay(
            result.snipedDefenses,
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        renderElementRangesByIds(mapConfig, result.airDefenses);
    };

    var renderArcherAnchor = function(result, mapConfig) {
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
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        renderElementRangesByIds(mapConfig, result.aimingDefenses);
    };

    var renderHogCCLure = function(result, mapConfig) {
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
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        renderElementRangesByTypeName(mapConfig, "ClanCastle");
    };

    var renderHighHPUnderAirDef = function(result, mapConfig) {
        eachBuildingDisplay(
            result.outOfAirDefRange,
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        eachBuildingDisplay(
            result.inAirDefRange,
            function (buildingContainer) {
                applyColour(buildingContainer, 0, 1, 0);
            }
        );
        renderElementRangesByTypeName(mapConfig, "AirDefense");
    };

    var eachBuildingDisplay = function(ids, operation) {
        _.each(
            _.map(
                ids,
                function (id) {
                    return _.find(
                        buildingsContainer.children,
                        function (buildingContainer) {
                            return buildingContainer.id == id;
                        }
                    );
                }
            ),
            operation
        );
    };

    var renderGrass = function(mapConfig) {
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
    };

    var render2dPreventTroopDrops = function(mapConfig) {
        var allPrevents = new createjs.Container();
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            if (element.noTroopDropBlock.size == 0) {
                continue;
            }
            var prevent = new createjs.Shape();
            prevent.graphics
                .beginFill("rgba(255,255,255,1)")
                .drawRect(
                    0,
                    0,
                    element.noTroopDropBlock.size * mapConfig.tileSize,
                    element.noTroopDropBlock.size * mapConfig.tileSize
                );
            prevent.x = element.noTroopDropBlock.x * mapConfig.tileSize;
            prevent.y = element.noTroopDropBlock.y * mapConfig.tileSize;
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
    };

    var render2dImageBuildings = function(mapConfig) {
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            if (element.typeName == "Wall") {
                renderWallImage(element, mapConfig);
                continue;
            }

            renderBuildingImage(element, mapConfig);
        }
    };

    var renderWallImage = function(element, mapConfig) {
        buildingsContainer.addChild(renderElementImage(element, mapConfig, wallSheet));
    };

    var renderBuildingImage = function(element, mapConfig) {
        var display = renderElementImage(element, mapConfig, buildingSheet);
        var grass = new createjs.Shape();
        grass.graphics
            .beginFill("#6fa414")
            .drawRect(
                0,
                0,
                element.block.size * mapConfig.tileSize,
                element.block.size * mapConfig.tileSize
            );
        display.addChildAt(grass, 0);
        buildingsContainer.addChild(display);
    };

    var renderElementImage = function(element, mapConfig, sheet) {
        var elementContainer = new createjs.Container();
        elementContainer.x = element.block.x * mapConfig.tileSize;
        elementContainer.y = element.block.y * mapConfig.tileSize;
        elementContainer.id = element.id;

        var bitmap = sheet.create(element, mapConfig);
        var finalWidth = bitmap.sourceRect.width * bitmap.scaleX;
        var finalHeight = bitmap.sourceRect.height * bitmap.scaleY;
        bitmap.x = ((element.block.size * mapConfig.tileSize) - finalWidth) / 2;
        bitmap.y = ((element.block.size * mapConfig.tileSize) - finalHeight) / 2;
        elementContainer.addChild(bitmap);

        return elementContainer;
    };
    
    var setAssets = function(newAssets) {
        buildingSheet = new BuildingSpriteSheet(newAssets.buildings);
        wallSheet = new WallSpriteSheet(newAssets.walls);
        render();
    };

    var render = function() {
        stage.clear();
        bgContainer.removeAllChildren();
        extrasContainer.removeAllChildren();
        buildingsContainer.removeAllChildren();

        if (buildingSheet == null || wallSheet == null) {
            var text = new createjs.Text("Loading Assets", "20px Arial", "#000000");
            text.y = 30;
            extrasContainer.addChild(text);
            stage.update();
            return;
        }

        if (!model.hasReport()) {
            stage.update();
            return;
        }

        render2d();

        stage.update();
    };

    /*function renderSpriteSheetDebug() {
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
    }*/

    function render2d() {
        // TODO: Get map/coordinate system config from api/backend
        var mapTiles = 44;
        var borderTiles = 1;
        var mapConfig = {
            mapTiles: mapTiles,
            borderTiles: borderTiles,
            tileSize: Math.min($(canvas).width(), $(canvas).height()) / (mapTiles + 2 * borderTiles)
        };
        renderGrass(mapConfig);
        //render2dRandomSolidColourElements(mapConfig);
        render2dPreventTroopDrops(mapConfig);
        render2dImageBuildings(mapConfig);
        renderActiveRule(mapConfig);
    }
    
    return {
        render: render,
        setAssets: setAssets,
        canvas: canvas
    };
})(document);