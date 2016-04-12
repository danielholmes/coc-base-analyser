package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

class VillageAnalyser(private val rulesByThLevel: Map[Int, Set[Rule]]) {
  def analyse(village: Village): Option[AnalysisReport] = {
    village.townHallLevel
      .flatMap(rulesByThLevel.get)
      .map(_.map(rule => rule.analyse(village)))
      .map(AnalysisReport(village, _))
  }
}
