"use strict";

var MapDisplay2d = function(canvas, mapConfig, model, displaySettings) {
    var stage = new createjs.Stage(canvas);
    stage.autoClear = false;
    var bgContainer = new createjs.Container();
    stage.addChild(bgContainer);
    var buildingsContainer = new createjs.Container();
    stage.addChild(buildingsContainer);
    var extrasContainer = new createjs.Container();
    stage.addChild(extrasContainer);

    var successColour = "#77ff77";
    var failColour = "#ff4444";

    var buildingSheet = null;
    var wallSheet = null;

    var randomColour = function(seed) {
        var numberSeed = (seed.length * 5.7) % 30;
        return '#' + Math.floor(numberSeed / 30 * 16777215).toString(16);
    };

    var renderElementRanges = function(mapDimensions, elements) {
        _.chain(elements)
            .map(function (elementToDraw) {
                switch (elementToDraw.range.typeName) {
                    case "Sector":
                        return createSectorElementRangeDisplay(mapDimensions, elementToDraw);
                    case "Circular":
                        return createCircularElementRangeDisplay(mapDimensions, elementToDraw);
                    default:
                        console.log("Can't render", elementToDraw.range)
                }
            })
            .each(function (circle) {
                extrasContainer.addChild(circle);
            });
    };

    var createSectorElementRangeDisplay = function(mapDimensions, element) {
        var angleSizeRadians = element.range.angleSize / 180 * Math.PI;
        var minLine = createSectorLineDetails(Math.PI / 2, element.range.innerSize, element.range.outerSize);
        var maxLine = createSectorLineDetails(Math.PI / 2 - angleSizeRadians, element.range.innerSize, element.range.outerSize);

        var display = new createjs.Shape();
        display.x = mapDimensions.toCanvasCoord(element.block.x + element.block.size / 2);
        display.y = mapDimensions.toCanvasCoord(element.block.y + element.block.size / 2);

        display.graphics
            .beginStroke("#ffffff")
            .moveTo(mapDimensions.toCanvasSize(minLine.coord1.x), mapDimensions.toCanvasSize(minLine.coord1.y))
            .lineTo(mapDimensions.toCanvasSize(minLine.coord2.x), mapDimensions.toCanvasSize(minLine.coord2.y))
            .arc(0, 0, mapDimensions.toCanvasSize(element.range.outerSize), 0, angleSizeRadians)
            .lineTo(mapDimensions.toCanvasSize(maxLine.coord1.x), mapDimensions.toCanvasSize(maxLine.coord1.y))
            .arc(0, 0, mapDimensions.toCanvasSize(element.range.innerSize), angleSizeRadians, 0, true);

        display.rotation = 180 + element.range.angle + (element.range.angleSize - element.range.angle) / 2;
        return display;
    };

    var createSectorLineDetails = function(angle, innerSize, outerSize) {
        return {
            coord1: {
                x: Math.sin(angle) * innerSize,
                y: Math.cos(angle) * innerSize
            },
            coord2: {
                x: Math.sin(angle) * outerSize,
                y: Math.cos(angle) * outerSize
            }
        };
    };

    var createLine = function(coord1, coord2, colour, mapDimensions) {
        var line = new createjs.Shape();
        line.graphics
            .beginStroke(colour)
            .moveTo(0, 0)
            .lineTo(
                mapDimensions.toCanvasSize(coord1.x - coord2.x),
                mapDimensions.toCanvasSize(coord1.y - coord2.y)
            );
        line.x = mapDimensions.toCanvasCoord(coord2.x);
        line.y = mapDimensions.toCanvasCoord(coord2.y);
        return line;
    };

    var createCircularElementRangeDisplay = function(mapDimensions, element) {
        var allInfo = new createjs.Container();
        var outerCircle = new createjs.Shape();
        outerCircle.graphics
            .beginStroke("#ffffff")
            .beginFill("rgba(255,255,255,0.05)")
            .drawCircle(
                0,
                0,
                mapDimensions.toCanvasSize(element.range.outer)
            );
        allInfo.addChild(outerCircle);

        if (element.range.inner) {
            var innerCircle = new createjs.Shape();
            innerCircle.graphics
                .beginStroke("#ffaaaa")
                .beginFill("rgba(255,80,80,0.05)")
                .drawCircle(
                    0,
                    0,
                    mapDimensions.toCanvasSize(element.range.inner)
                );
            allInfo.addChild(innerCircle);
        }

        var lineSize = 1;
        var vert = new createjs.Shape();
        vert.graphics
            .beginStroke("#ffffff")
            .moveTo(0, -mapDimensions.toCanvasSize(lineSize / 2))
            .lineTo(0, mapDimensions.toCanvasSize(lineSize / 2));
        allInfo.addChild(vert);
        var hor = new createjs.Shape();
        hor.graphics
            .beginStroke("#ffffff")
            .moveTo(-mapDimensions.toCanvasSize(lineSize / 2), 0)
            .lineTo(mapDimensions.toCanvasSize(lineSize / 2), 0);
        allInfo.addChild(hor);

        allInfo.x = mapDimensions.toCanvasCoord(element.block.x + element.block.size / 2);
        allInfo.y = mapDimensions.toCanvasCoord(element.block.y + element.block.size / 2);

        return allInfo;
    };

    var renderElementRangesByIds = function(mapDimensions, ids) {
        renderElementRanges(mapDimensions, model.getVillageElementsByIds(ids));
    };

    var renderElementRangesByTypeName = function(mapDimensions, typeName) {
        renderElementRanges(
            mapDimensions,
            model.getVillageElementsByTypeName(typeName)
        );
    };

    var renderWizardTowersOutOfHoundPositions = function(result, mapDimensions) {
        eachBuildingDisplay(
            result.outOfRange,
            function (buildingContainer) {
                applyColour(buildingContainer, 0, 1, 0);
            }
        );

        var inRangeTowerIds = _.pluck(result.inRange, 'tower');
        eachBuildingDisplay(
            inRangeTowerIds,
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        eachBuildingDisplay(
            _.pluck(result.inRange, 'airDefense'),
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0.8, 0.8);
            }
        );
        _.chain(result.inRange)
            .pluck('houndTarget')
            .each(function(target) {
                var display = new createjs.Shape();
                display.alpha = 0.5;
                display.graphics
                    .beginFill(failColour)
                    .drawRect(0, 0, mapDimensions.toCanvasSize(target.size), mapDimensions.toCanvasSize(target.size));
                display.x = mapDimensions.toCanvasCoord(target.x);
                display.y = mapDimensions.toCanvasCoord(target.y);
                extrasContainer.addChild(display);
            });
        renderElementRangesByIds(mapDimensions, _.union(inRangeTowerIds, result.outOfRange));
    };

    var renderQueenWalkedAirDefense = function(result, mapDimensions) {
        eachBuildingDisplay(
            result.nonReachableAirDefs,
            function (buildingContainer) {
                applyColour(buildingContainer, 0, 1, 0);
            }
        );
        eachBuildingDisplay(
            _.pluck(result.attackings, 'targetingId'),
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        _.each(
            result.attackings,
            function(attacking) {
                extrasContainer.addChild(createLine(attacking.hitPoint, attacking.standingPosition, failColour, mapDimensions));
            }
        );
    };

    var renderEnoughPossibleTrapLocations = function(result, mapDimensions) {
        var colour = result.success ? successColour : failColour;
        var display = new createjs.Shape();
        display.alpha = 0.3;
        _.each(
            model.getReport().village.possibleInternalLargeTraps,
            function(trap) {
                display.graphics
                    .beginFill(colour)
                    .drawRect(
                        mapDimensions.toCanvasCoord(trap.x),
                        mapDimensions.toCanvasCoord(trap.y),
                        mapDimensions.toCanvasSize(2),
                        mapDimensions.toCanvasSize(2)
                    )
                    .endFill();
            }
        );
        extrasContainer.addChild(display);
    };

    var renderActiveRule = function(mapDimensions) {
        if (!model.hasActiveRule()) {
            return;
        }

        var result = _.findWhere(model.getReport().results, { 'code': model.getActiveRuleCode() });
        switch (result.code) {
            case 'ArcherAnchor':
                renderArcherAnchor(result, mapDimensions);
                break;
            case 'HogCCLure':
                renderHogCCLure(result, mapDimensions);
                break;
            case 'HighHPUnderAirDef':
                renderHighHPUnderAirDef(result, mapDimensions);
                break;
            case 'AirSnipedDefense':
                renderAirSnipedDefense(result, mapDimensions);
                break;
            case 'MinimumCompartments':
                renderMinimumCompartments(result, mapDimensions);
                break;
            case 'BKSwappable':
                renderBKSwappable(result, mapDimensions);
                break;
            case 'WizardTowersOutOfHoundPositions':
                renderWizardTowersOutOfHoundPositions(result, mapDimensions);
                break;
            case 'QueenWalkedAirDefense':
                renderQueenWalkedAirDefense(result, mapDimensions);
                break;
            case 'QueenWontLeaveCompartment':
                renderQueenWontLeaveCompartment(result, mapDimensions);
                break;
            case 'EnoughPossibleTrapLocations':
                renderEnoughPossibleTrapLocations(result, mapDimensions);
                break;
            default:
                console.error('Don\'t know how to render active rule: ' + result.code);
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

    var renderBKSwappable = function(result, mapDimensions) {
        var bk = model.getVillageElementByTypeName("BarbarianKingAltar");
        if (bk == null) {
            return;
        }

        if (!result.success) {
            var exposedMask = new createjs.Shape();
            exposedMask.graphics.beginFill(successColour);
            _.each(result.exposedTiles, function (tile) {
                exposedMask.graphics.drawRect(
                    mapDimensions.toCanvasCoord(tile.x),
                    mapDimensions.toCanvasCoord(tile.y),
                    mapDimensions.toCanvasSize(1),
                    mapDimensions.toCanvasSize(1)
                );
            });

            var bkRadiusFill = new createjs.Shape();
            bkRadiusFill.graphics
                .beginFill(failColour)
                .drawCircle(
                    0,
                    0,
                    mapDimensions.toCanvasSize(bk.range.outer)
                );
            bkRadiusFill.x = mapDimensions.toCanvasCoord(bk.block.x + bk.block.size / 2);
            bkRadiusFill.y = mapDimensions.toCanvasCoord(bk.block.y + bk.block.size / 2);
            bkRadiusFill.alpha = 0.6;
            bkRadiusFill.mask = exposedMask;

            extrasContainer.addChild(bkRadiusFill);
        }

        renderElementRangesByTypeName(mapDimensions, "BarbarianKingAltar");
    };

    var renderQueenWontLeaveCompartment = function(result, mapDimensions) {
        var compartment = model.getVillageArcherQueenCompartment();
        if (compartment == null) {
            return;
        }

        highlightCompartment(
            compartment,
            result.success ? successColour : failColour,
            mapDimensions
        )
    };

    var renderMinimumCompartments = function(result, mapDimensions) {
        _.each(
            model.getVillageCompartmentsByIds(result.compartments),
            function(compartment) {
                highlightCompartment(compartment, randomColour(compartment.walls.join("|")), mapDimensions);
            }
        );
    };

    var highlightCompartment = function(compartment, colour, mapDimensions) {
        var innerDisplay = new createjs.Shape();
        innerDisplay.graphics.beginFill(colour);
        _.each(compartment.innerTiles, function(innerTile) {
            innerDisplay.graphics.drawRect(
                mapDimensions.toCanvasCoord(innerTile.x),
                mapDimensions.toCanvasCoord(innerTile.y),
                mapDimensions.toCanvasSize(1),
                mapDimensions.toCanvasSize(1)
            );
        });
        innerDisplay.alpha = 0.85;
        extrasContainer.addChild(innerDisplay);

        var wallDisplay = new createjs.Shape();
        wallDisplay.graphics.beginFill(colour);
        _.each(compartment.walls, function(wallId) {
            var element = model.getVillageElementById(wallId);
            wallDisplay.graphics.drawRect(
                mapDimensions.toCanvasCoord(element.block.x),
                mapDimensions.toCanvasCoord(element.block.y),
                mapDimensions.toCanvasSize(1),
                mapDimensions.toCanvasSize(1)
            );
        });
        wallDisplay.alpha = 0.5;
        extrasContainer.addChild(wallDisplay);
    };

    var renderAirSnipedDefense = function(result, mapDimensions) {
        eachBuildingDisplay(
            _.pluck(result.attackPositions, 'targetingId'),
            function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            }
        );
        _.chain(result.attackPositions)
            .map(function(targeting) {
                return createLine(targeting.hitPoint, targeting.startPosition, failColour, mapDimensions);
            })
            .each(function(display) { extrasContainer.addChild(display); });
        renderElementRangesByIds(mapDimensions, result.airDefenses);
    };

    var renderArcherAnchor = function(result, mapDimensions) {
        _.chain(result.targetings)
            .map(function (targeting) {
                extrasContainer.addChild(createLine(targeting.hitPoint, targeting.standingPosition, failColour, mapDimensions));
                return _.findWhere(buildingsContainer.children, { 'id': targeting.targetingId });
            })
            .each(function(buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            });
        renderElementRangesByIds(mapDimensions, result.aimingDefenses);
    };

    var renderHogCCLure = function(result, mapDimensions) {
        _.chain(result.targetings)
            .map(function (targeting) {
                extrasContainer.addChild(createLine(targeting.hitPoint, targeting.startPosition, failColour, mapDimensions));
                return _.findWhere(buildingsContainer.children, { 'id': targeting.targetingId });
            })
            .each(function (buildingContainer) {
                applyColour(buildingContainer, 1, 0, 0);
            });
        renderElementRangesByTypeName(mapDimensions, "ClanCastle");
    };

    var renderHighHPUnderAirDef = function(result, mapDimensions) {
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
        renderElementRangesByTypeName(mapDimensions, "AirDefense");
    };

    var eachBuildingDisplay = function(ids, operation) {
        _.chain(ids)
            .map(function (id) {
                var building = _.findWhere(buildingsContainer.children, { 'id': id });
                if (building == null) {
                    console.error("No building with id " + id);
                }
                return building;
            })
            .each(operation);
    };

    var renderGrass = function(mapDimensions) {
        var grass = new createjs.Shape();
        grass.graphics
            .beginFill("#598c02")
            .drawRect(
                mapDimensions.toCanvasCoord(0),
                mapDimensions.toCanvasCoord(0),
                mapDimensions.toCanvasSize(mapDimensions.totalTiles),
                mapDimensions.toCanvasSize(mapDimensions.totalTiles)
            );
        grass.graphics
            .beginFill("#8cbf15")
            .drawRect(
                mapDimensions.toCanvasCoord(mapDimensions.borderTiles),
                mapDimensions.toCanvasCoord(mapDimensions.borderTiles),
                mapDimensions.toCanvasSize(mapDimensions.mapTiles),
                mapDimensions.toCanvasSize(mapDimensions.mapTiles)
            );
        _.chain(_.range(mapDimensions.mapTiles))
            .each(function(col) {
                _.chain(_.range(mapDimensions.mapTiles))
                    .each(function (row) {
                        if ((row + col) % 2 == 0) {
                            grass.graphics
                                .beginFill("#87ba10")
                                .drawRect(
                                    mapDimensions.toCanvasCoord(mapDimensions.borderTiles + col),
                                    mapDimensions.toCanvasCoord(mapDimensions.borderTiles + row),
                                    mapDimensions.toCanvasSize(1),
                                    mapDimensions.toCanvasSize(1)
                                );
                        }
                    })
            });
        bgContainer.addChild(grass);
    };

    var render2dPreventTroopDrops = function(mapDimensions) {
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
                    mapDimensions.toCanvasSize(element.noTroopDropBlock.size),
                    mapDimensions.toCanvasSize(element.noTroopDropBlock.size)
                );
            prevent.x = mapDimensions.toCanvasCoord(element.noTroopDropBlock.x);
            prevent.y = mapDimensions.toCanvasCoord(element.noTroopDropBlock.y);
            allPrevents.addChild(prevent);
        }
        allPrevents.filters = [
            new createjs.ColorFilter(
                1, 1, 1, 0.15,
                0, 0, 0, 0
            )
        ];
        allPrevents.cache(0, 0, 1000, 1000);
        bgContainer.addChild(allPrevents);
    };

    var render2dImageBuildings = function(mapDimensions) {
        for (var i in model.getReport().village.elements) {
            var element = model.getReport().village.elements[i];
            if (element.typeName == "Wall") {
                renderWallImage(element, mapDimensions);
                continue;
            }

            renderBuildingImage(element, mapDimensions);
        }
    };

    var renderWallImage = function(element, mapDimensions) {
        buildingsContainer.addChild(renderElementImage(element, mapDimensions, wallSheet));
    };

    var renderBuildingImage = function(element, mapDimensions) {
        var display = renderElementImage(element, mapDimensions, buildingSheet);
        var grass = new createjs.Shape();
        grass.graphics
            .beginFill("#6fa414")
            .drawRect(
                0,
                0,
                mapDimensions.toCanvasSize(element.block.size),
                mapDimensions.toCanvasSize(element.block.size)
            );
        display.addChildAt(grass, 0);
        buildingsContainer.addChild(display);
    };

    var renderElementImage = function(element, mapDimensions, sheet) {
        var elementContainer = new createjs.Container();
        elementContainer.x = mapDimensions.toCanvasCoord(element.block.x);
        elementContainer.y = mapDimensions.toCanvasCoord(element.block.y);
        elementContainer.id = element.id;

        var bitmap = sheet.create(element, mapDimensions);
        var finalWidth = bitmap.sourceRect.width * bitmap.scaleX;
        var finalHeight = bitmap.sourceRect.height * bitmap.scaleY;
        bitmap.x = (mapDimensions.toCanvasSize(element.block.size) - finalWidth) / 2;
        bitmap.y = (mapDimensions.toCanvasSize(element.block.size) - finalHeight) / 2;
        elementContainer.addChild(bitmap);

        return elementContainer;
    };
    
    var setAssets = function(newAssets) {
        buildingSheet = new RedMoonBuildingSpriteSheet(newAssets.redMoonBuildings);
        wallSheet = new RedMoonWallSpriteSheet(newAssets.redMoonWalls);
        render();
    };

    var render = function() {
        stage.clear();
        bgContainer.removeAllChildren();
        extrasContainer.removeAllChildren();
        buildingsContainer.removeAllChildren();

        if (buildingSheet == null || wallSheet == null) {
            var text = new createjs.Text("Loading Images" + new Array(1 + parseInt(_.now() / 1000) % 4).join('.'), "14px monospace", "#000000");
            text.x = 5;
            text.y = 5;
            extrasContainer.addChild(text);
            stage.update();

            setTimeout(render, 1000);

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

    var renderGrid = function(mapDimensions) {
        if (!displaySettings.getShowGrid()) {
            return;
        }

        var colour = "#ff0000";
        var alpha = 0.3;
        var mapIndexes = _.range(mapDimensions.totalTiles);
        _.chain(mapIndexes)
            .map(function(col) {
                var strokeSize = col % 5 == 0 ? 2 : 1;
                var line = new createjs.Shape();
                line.x = mapDimensions.toCanvasCoord(col);
                line.y = 0;
                line.alpha = alpha;
                line.graphics
                    .beginStroke(colour)
                    .setStrokeStyle(strokeSize)
                    .moveTo(0, 0)
                    .lineTo(0, mapDimensions.toCanvasCoord(mapDimensions.totalTiles));
                return line;
            })
            .each(function(display) { extrasContainer.addChild(display); });

        _.chain(mapIndexes)
            .map(function(row) {
                var strokeSize = row % 5 == 0 ? 2 : 1;
                var line = new createjs.Shape();
                line.x = 0;
                line.y = mapDimensions.toCanvasCoord(row);
                line.alpha = alpha;
                line.graphics
                    .beginStroke(colour)
                    .setStrokeStyle(strokeSize)
                    .moveTo(0, 0)
                    .lineTo(mapDimensions.toCanvasCoord(mapDimensions.totalTiles), 0);
                return line;
            })
            .each(function(display) { extrasContainer.addChild(display); });

        _.chain(mapIndexes)
            .filter(function(mapIndex) { return mapIndex != 0 && mapIndex % 5 == 0; })
            .map(function(mapIndex) {
                var textSize = Math.ceil(canvas.width / 50);

                var rowLeft = new createjs.Text(mapIndex, textSize + "px monospace", colour);
                rowLeft.x = 0;
                rowLeft.y = mapDimensions.toCanvasCoord(mapIndex);
                rowLeft.textBaseline = "top";

                var colTop = new createjs.Text(mapIndex, textSize + "px monospace", colour);
                colTop.x = mapDimensions.toCanvasCoord(mapIndex);
                colTop.y = 0;
                colTop.textBaseline = "top";

                var rowRight = new createjs.Text(mapIndex, textSize + "px monospace", colour);
                rowRight.x = mapDimensions.toCanvasCoord(mapDimensions.totalTiles - mapDimensions.hiddenBorder);
                rowRight.y = mapDimensions.toCanvasCoord(mapIndex);
                rowRight.textBaseline = "top";
                rowRight.textAlign = "right";

                var colBottom = new createjs.Text(mapIndex, textSize + "px monospace", colour);
                colBottom.x = mapDimensions.toCanvasCoord(mapIndex);
                colBottom.y = mapDimensions.toCanvasCoord(mapDimensions.totalTiles - mapDimensions.hiddenBorder);
                colBottom.textBaseline = "bottom";

                return [rowLeft, colTop, rowRight, colBottom];
            })
            .flatten()
            .each(function(display) { extrasContainer.addChild(display); });
    };

    var render2d = function() {
        var mapDimensions = new MapDimensions(_.extend(mapConfig, {
            totalTiles: mapConfig.mapTiles + mapConfig.borderTiles * 2,
            canvasSize: Math.min($(canvas).width(), $(canvas).height())
        }));
        renderGrass(mapDimensions);
        //render2dRandomSolidColourElements(mapDimensions);
        render2dPreventTroopDrops(mapDimensions);
        render2dImageBuildings(mapDimensions);
        renderActiveRule(mapDimensions);
        renderGrid(mapDimensions);
    };

    var MapDimensions = function(props) {
        var hiddenBorder = 2;
        var tileSize = props.canvasSize / (props.totalTiles - 2 * hiddenBorder);

        var toCanvasCoord = function(coord) {
            return (coord - hiddenBorder) * tileSize;
        };

        var toCanvasSize = function(size) {
            return size * tileSize;
        };

        return {
            mapTiles: props.mapTiles,
            borderTiles: props.borderTiles,
            totalTiles: props.totalTiles,
            hiddenBorder: hiddenBorder,
            toCanvasCoord: toCanvasCoord,
            toCanvasSize: toCanvasSize
        };
    };
    
    displaySettings.showGridChanged.add(_.bind(render, this));
    
    return {
        render: render,
        setAssets: setAssets,
        canvas: canvas
    };
};