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
    
    var getVillageElementById = function(id) {
        return _.find(
            currentReport.village.elements,
            function(element) { return element.id == id; }
        );
    };
    
    var getVillageElementsByIds = function(ids) {
        return _.filter(currentReport.village.elements, function (element) {
            return _.contains(ids, element.id);
        });
    };

    var getVillageCompartmentsByIds = function(ids) {
        return _.filter(currentReport.village.wallCompartments, function (compartment) {
            return _.contains(ids, compartment.id);
        });
    };

    var getVillageElementsByTypeName = function(typeName) {
        return _.filter(currentReport.village.elements, function (element) {
            return element.typeName == typeName;
        });
    };
    
    var getVillageElementByTypeName = function(typeName) {
        return _.find(currentReport.village.elements, function (element) {
            return element.typeName == typeName;
        });
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
        getVillageElementById: getVillageElementById,
        getVillageElementsByIds: getVillageElementsByIds,
        getVillageElementsByTypeName: getVillageElementsByTypeName,
        getVillageElementByTypeName: getVillageElementByTypeName,
        getVillageCompartmentsByIds: getVillageCompartmentsByIds,

        hasActiveRule: hasActiveRule,
        getActiveRuleName: getActiveRuleName,
        setActiveRuleName: setActiveRuleName,
        clearActiveRuleName: clearActiveRuleName,

        reportChanged: reportChanged,
        ruleChanged: ruleChanged
    };
})();