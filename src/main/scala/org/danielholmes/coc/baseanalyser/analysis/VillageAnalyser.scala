package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village
import org.scalactic.anyvals.PosInt

class VillageAnalyser(private val rulesByThLevel: Map[PosInt, Set[Rule]]) {
  require(rulesByThLevel.nonEmpty)

  lazy val minTownHallLevel = rulesByThLevel.keys.min

  lazy val maxTownHallLevel = rulesByThLevel.keys.max

  def analyse(village: Village): Option[AnalysisReport] = {
    village.townHallLevel
      .flatMap(rulesByThLevel.get)
      .map(_.map(rule => rule.analyse(village)))
      .map(AnalysisReport(village, _))
  }
}
