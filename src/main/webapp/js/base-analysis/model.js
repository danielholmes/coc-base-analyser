"use strict";

var Model = function() {
    var currentReport = null;
    var activeRuleCode = null;
    var reportChanged = new signals.Signal();
    var ruleChanged = new signals.Signal();

    var setReport = function(newReport) {
        if (newReport == currentReport) {
            return;
        }

        currentReport = newReport;
        clearActiveRule();
        reportChanged.dispatch();
    };

    var clearReport = function() {
        setReport(null);
    };
    
    var getReport = function() {
        return currentReport;
    };
    
    var getVillageElementById = function(id) {
        return _.findWhere(currentReport.village.elements, { 'id': id });
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
    
    var getVillageArcherQueenCompartment = function() {
        return _.find(
            currentReport.village.wallCompartments,
            function(compartment) {
                return _.some(
                    compartment.elementIds,
                    function(elementId) {
                        return getVillageElementById(elementId).typeName == "ArcherQueenAltar";
                    }
                );
            }
        );
    };

    var getVillageElementsByTypeName = function(typeName) {
        return _.where(currentReport.village.elements, { 'typeName': typeName });
    };
    
    var getVillageElementByTypeName = function(typeName) {
        return _.findWhere(currentReport.village.elements, { 'typeName': typeName });
    };

    var hasReport = function() {
        return currentReport != null;
    };

    var hasActiveRule = function() {
        return activeRuleCode != null;
    };

    var getActiveRuleCode = function() {
        return activeRuleCode;
    };

    var clearActiveRule = function() {
        setActiveRuleByCode(null);
    };

    var setActiveRuleByCode = function(newActiveRuleCode) {
        if (newActiveRuleCode == activeRuleCode) {
            return;
        }

        if (newActiveRuleCode != null && _.findWhere(report.results, { 'code': newActiveRuleCode }) == null) {
            console.error("No rule code in current report:", newActiveRuleCode, report);
            return;
        }

        activeRuleCode = newActiveRuleCode;
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
        getVillageArcherQueenCompartment: getVillageArcherQueenCompartment,

        hasActiveRule: hasActiveRule,
        getActiveRuleCode: getActiveRuleCode,
        setActiveRuleByCode: setActiveRuleByCode,
        clearActiveRule: clearActiveRule,

        reportChanged: reportChanged,
        ruleChanged: ruleChanged
    };
};