$(document).ready(function() {
    var searchForm = $("#searchForm");
    var searchButton = searchForm.find("input[type='submit']");
    var loading = searchForm.find(".loading");
    var canvas = document.getElementById("villageImage");
    var stage = new createjs.Stage(canvas);

    // Loading
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

    // Render
    function render2d(village) {
        var tileSize = Math.floor(Math.min(canvas.width, canvas.height) / 44);

        // Grass
        var grass = new createjs.Shape();
        grass.graphics.beginFill("#99dd99").drawRect(0, 0, canvas.width, canvas.height);
        stage.addChild(grass);

        // Blocks
        for (var i in village.elements) {
            var element = village.elements[i];
            var seed = (element.typeName.length * 5.7) % 30;
            var colour = '#' + Math.floor(seed / 30 * 16777215).toString(16);
            var shape = new createjs.Shape();
            shape.graphics.beginFill(colour).drawRect(0, 0, element.block.width * tileSize, element.block.height * tileSize);
            shape.x = element.block.x * tileSize;
            shape.y = element.block.y * tileSize;
            stage.addChild(shape);
        }

        // CC Radius
        _.each(
            _.map(
                _.filter(village.elements, function(element) { return element.typeName == "ClanCastle"; }),
                function(clanCastle) {
                    var radius = new createjs.Shape();
                    radius.graphics.beginStroke("#ffffff").drawCircle(0, 0, clanCastle.range.outer * tileSize);
                    radius.x = (clanCastle.block.x + clanCastle.block.width / 2) * tileSize;
                    radius.y = (clanCastle.block.y + clanCastle.block.height / 2) * tileSize;
                    return radius;
                }
            ),
            function(castle) { stage.addChild(castle); }
        );

        stage.update();
    }
    function clearRender() {
        stage.removeAllChildren();
        stage.update();
    }

    // Submission
    searchForm.on("submit", function(){
        if (runningAnalysis) {
            return false;
        }

        startLoading();
        clearRender();

        var userName = $("#userNameField").val();
        $.getJSON("/village-analysis/" + encodeURI(userName))
            .done(function(response){
                render2d(response.village);
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
});