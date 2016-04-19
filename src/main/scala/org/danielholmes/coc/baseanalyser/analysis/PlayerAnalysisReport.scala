package org.danielholmes.coc.baseanalyser.analysis

case class PlayerAnalysisReport(userName: String, townHallLevel: Int, villageReport: Option[AnalysisReport])
