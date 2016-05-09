package org.danielholmes.coc.baseanalyser.web

case class AnalysisReportSummaryViewModel(townHallLevel: Int, resultSummaries: Set[ResultSummaryViewModel])

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel])

case class CantAnalyseVillageViewModel(village: VillageViewModel, message: String)

case class ExceptionViewModel(uri: String, exceptionType: String, message: String, trace: List[String])

case class BaseAnalysisViewModel(
  mapTiles: Int,
  borderTiles: Int,
  clanName: String,
  playerIgn: String,
  layoutDescription: String,
  report: String,
  warning: Option[String],
  times: BaseAnalysisProfilingViewModel
)

case class BaseAnalysisProfilingViewModel(
  connection: String,
  analysis: String,
  times: Seq[(String, Seq[(String, String)])]
)
