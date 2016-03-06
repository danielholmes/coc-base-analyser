$(document).ready(function() {
    var searchForm = $("#searchForm");
    var searchButton = searchForm.find("input[type='submit']");
    var loading = searchForm.find(".loading");

    // Loading
    var runningAnalysis = false;
    function startAnalysis() {
        if (runningAnalysis) {
            return;
        }
        searchButton.attr("disabled", "disabled");
        loading.show();
        runningAnalysis = true;
    }

    function stopAnalysis() {
        if (!runningAnalysis) {
            return;
        }
        searchButton.removeAttr("disabled");
        runningAnalysis = false;
        loading.fadeOut();
    }

    // Render
    function render(village) {
        var canvas = document.getElementById("villageImage");
        var stage = new createjs.Stage(canvas);
        var tileSize = Math.floor(Math.min(canvas.width, canvas.height) / 44);

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

        stage.update();
    }

    // Submission
    searchForm.on("submit", function(){
        if (runningAnalysis) {
            return false;
        }

        startAnalysis();

        var userName = $("#userNameField").val();
        $.getJSON("/village-analysis/" + encodeURI(userName))
            .done(function(response){
                render(response.village);
            })
            .fail(function(response){
                if (response.status == 404) {
                    alert("user not found in approved clans");
                    return false;
                }
            })
            .always(function(){
                stopAnalysis();
            });

        return false;
    });
});