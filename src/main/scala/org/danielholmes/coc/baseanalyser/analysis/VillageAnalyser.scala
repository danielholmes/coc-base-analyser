package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

class VillageAnalyser(private val rules: Set[Rule]) {
  def analyse(village: Village): AnalysisReport = {
    AnalysisReport(village, rules.map(_.analyse(village)))
  }
}

object VillageAnalyser {
  val analysers = Map(
    8 -> new VillageAnalyser(Set(new HogCCLureRule, new HighHPUnderAirDefRule))
  )

  def analyse(village: Village): Option[AnalysisReport] = {
    getByVillage(village).map(_.analyse(village))
  }

  private def getByVillage(village: Village): Option[VillageAnalyser] = {
    //village.townHallLevel.flatMap(analysers.get)
    analysers.get(8)
  }
}
