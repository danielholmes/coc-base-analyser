'use strict';

var model = (function() {
    var currentReport = null;
    var activeRuleName = null;
    var reportChanged = new signals.Signal();
    var ruleChanged = new signals.Signal();

    var setReport = function(newReport) {
        if (newReport == currentReport) {
            return;
        }

        currentReport = newReport;
        clearActiveRuleName();
        reportChanged.dispatch();
    };

    var clearReport = function() {
        setReport(null);
    };
    
    var getReport = function() {
        return currentReport;
    };

    var hasReport = function() {
        return currentReport != null;
    };

    var hasActiveRule = function() {
        return activeRuleName != null;
    };

    var getActiveRuleName = function() {
        return activeRuleName;
    };

    var clearActiveRuleName = function() {
        setActiveRuleName(null);
    };

    var setActiveRuleName = function(newActiveRuleName) {
        if (newActiveRuleName == activeRuleName) {
            return;
        }
        activeRuleName = newActiveRuleName;
        ruleChanged.dispatch();
    };

    return {
        getReport: getReport,
        setReport: setReport,
        hasReport: hasReport,
        clearReport: clearReport,

        hasActiveRule: hasActiveRule,
        getActiveRuleName: getActiveRuleName,
        setActiveRuleName: setActiveRuleName,
        clearActiveRuleName: clearActiveRuleName,

        reportChanged: reportChanged,
        ruleChanged: ruleChanged
    };
})();