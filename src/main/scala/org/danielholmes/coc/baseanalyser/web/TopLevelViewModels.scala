package org.danielholmes.coc.baseanalyser.web

case class AnalysisReportSummaryViewModel(townHallLevel: Int, resultSummaries: Set[ResultSummaryViewModel])

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel])

case class CantAnalyseVillageViewModel(village: VillageViewModel, message: String)

case class ExceptionViewModel(uri: String, exceptionType: String, message: String, trace: List[String])
