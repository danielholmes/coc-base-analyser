package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

class VillageAnalyser(private val rules: Set[Rule]) {
  def analyse(village: Village): Option[AnalysisReport] = {
    Some(AnalysisReport(village, rules.map(_.analyse(village))))
  }
}
