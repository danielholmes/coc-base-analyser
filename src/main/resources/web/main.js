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

    // Submission
    searchForm.on("submit", function(){
        if (runningAnalysis) {
            return false;
        }

        startAnalysis();

        var userName = $("#userNameField").val();
        $.getJSON("/village-analysis/" + encodeURI(userName))
            .done(function(response){
                console.log("done", response);
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