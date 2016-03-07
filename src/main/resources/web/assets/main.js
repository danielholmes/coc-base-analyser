$(document).ready(function() {
    var searchForm = $("#searchForm");
    var searchButton = searchForm.find("input[type='submit']");
    var loading = searchForm.find(".loading");
    var canvas = document.getElementById("villageImage");
    var canvasContext = canvas.getContext("2d");
    var stage = new createjs.Stage(canvas);
    stage.autoClear = false;

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

    // Preload assets
    var assets = null;
    var queue = new createjs.LoadQueue();
    queue.on("complete", handleAssetsLoadComplete, this);
    queue.loadManifest([
        { id: "buildings", src:"assets/buildings-sprite.png" },
        { id: "walls", src:"assets/walls.png" }
    ]);
    function handleAssetsLoadComplete() {
        assets = {
            "buildings": queue.getResult("buildings"),
            "walls": queue.getResult("walls")
        };
        render();
    }
    render();

    // Analysis Progress
    var runningAnalysis = false;
    function startLoading() {
        if (runningAnalysis) {
            return;
        }
        searchButton.attr("disabled", "disabled");
        loading.show();
        runningAnalysis = true;
    }

    function stopLoading() {
        if (!runningAnalysis) {
            return;
        }
        searchButton.removeAttr("disabled");
        runningAnalysis = false;
        loading.fadeOut();
    }

    // Model
    var currentReport = null;
    function setCurrentReport(report) {
        currentReport = report;
        render();
    }
    function clearCurrentReport() {
        currentReport = null;
        render();
    }

    // Render
    function render() {
        stage.clear();
        stage.removeAllChildren();

        if (assets == null) {
            var text = new createjs.Text("Loading Assets", "20px Arial", "#000000");
            text.y = 30;
            stage.addChild(text);
            stage.update();
            return;
        }

        //renderSpriteSheetDebug();

        if (currentReport == null) {
            stage.update();
            return;
        }

        render2d();

        stage.update();
    }
    function renderSpriteSheetDebug() {
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
        var tileSize = Math.floor(Math.min(canvas.width, canvas.height) / 44);
        render2dGrass();
        //render2dRandomSolidColourElements(tileSize);
        render2dBuildingGrassBases(tileSize);
        render2dImageBuildings(tileSize);
        renderCCRadius(tileSize);
    }
    function render2dGrass() {
        canvasContext.fillStyle = "#8cbf15";
        canvasContext.fillRect(
            0,
            0,
            canvas.width,
            canvas.height
        );
    }
    function render2dImageBuildings(tileSize) {
        for (var i in currentReport.village.elements) {
            var element = currentReport.village.elements[i];
            if (element.typeName == "Wall") {
                renderWallImage(element, tileSize);
                continue;
            }

            renderBuildingImage(element, tileSize);
        }
    }
    function renderWallImage(element, tileSize) {
        renderElementImage(
            element,
            tileSize,
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
    function renderBuildingImage(element, tileSize) {
        var sheetDef = buildingsSheet[element.typeName];
        if (sheetDef == null) {
            console.error("Don't know how to render: " + element.typeName);
            return;
        }

        renderElementImage(
            element,
            tileSize,
            assets.buildings,
            sheetDef,
            element.level - 1
        )
    }
    function renderElementImage(element, tileSize, image, sheetDef, sheetIndex) {
        var widthRatio = element.block.width * tileSize / sheetDef.width;
        var heightRatio = element.block.height * tileSize / sheetDef.height;
        var useScale = Math.min(
            widthRatio,
            heightRatio
        );
        var finalWidth = sheetDef.width * useScale;
        var finalHeight = sheetDef.height * useScale;
        canvasContext.drawImage(
            image,
            sheetDef.x + sheetIndex * (sheetDef.width + sheetDef.gap) + sheetDef.gap / 2,
            sheetDef.y,
            sheetDef.width,
            sheetDef.height,
            element.block.x * tileSize + ((element.block.width * tileSize) - finalWidth) / 2,
            element.block.y * tileSize + ((element.block.height * tileSize) - finalHeight) / 2,
            finalWidth,
            finalHeight
        );
    }
    function render2dRandomSolidColourElements(tileSize) {
        for (var i in currentReport.village.elements) {
            var element = currentReport.village.elements[i];
            canvasContext.fillStyle = randomColour(element.typeName);
            canvasContext.fillRect(
                element.block.x * tileSize,
                element.block.y * tileSize,
                element.block.width * tileSize,
                element.block.height * tileSize
            );
        }
    }
    function render2dBuildingGrassBases(tileSize) {
        canvasContext.fillStyle = "#6fa414";
        _.each(
            _.filter(
                currentReport.village.elements,
                function(element) { return element.typeName != "Wall"; }
            ),
            function(building) {
                canvasContext.fillRect(
                    building.block.x * tileSize,
                    building.block.y * tileSize,
                    building.block.width * tileSize,
                    building.block.height * tileSize
                );
            }
        );
    }
    function renderCCRadius(tileSize) {
        _.each(
            _.filter(currentReport.village.elements, function(element) { return element.typeName == "ClanCastle"; }),
            function(clanCastle) {
                canvasContext.strokeStyle = "#ffffff";
                canvasContext.beginPath();
                canvasContext.arc(
                    (clanCastle.block.x + clanCastle.block.width / 2) * tileSize,
                    (clanCastle.block.y + clanCastle.block.height / 2) * tileSize,
                    clanCastle.range.outer * tileSize,
                    0,
                    2 * Math.PI
                );
                canvasContext.stroke();
            }
        );
    }

    // UI
    searchForm.on("submit", function(){
        if (runningAnalysis) {
            return false;
        }

        startLoading();
        clearCurrentReport();

        var userName = $("#userNameField").val();
        $.getJSON("/village-analysis/" + encodeURI(userName))
            .done(function(response){
                setCurrentReport(response);
            })
            .fail(function(response){
                if (response.status == 404) {
                    alert("user not found in approved clans");
                    return false;
                }
            })
            .always(function(){
                stopLoading();
            });

        return false;
    });

    // utils
    function randomColour(seed) {
        var numberSeed = (seed.length * 5.7) % 30;
        return '#' + Math.floor(numberSeed / 30 * 16777215).toString(16);
    }
});