package org.danielholmes.coc.baseanalyser.analysis

import java.time.Duration

import org.danielholmes.coc.baseanalyser.model.Village
import org.danielholmes.coc.baseanalyser.util.TimedInvocation
import org.scalactic.anyvals.PosInt

class VillageAnalyser(private val rulesByThLevel: Map[PosInt, Set[Rule]]) {
  require(rulesByThLevel.nonEmpty)

  lazy val minTownHallLevel = rulesByThLevel.keys.min

  lazy val maxTownHallLevel = rulesByThLevel.keys.max

  def analyse(village: Village): Option[AnalysisReport] = {
    // NOTE: Times taken are thrown out if done in parallel
    village.townHallLevel
      .flatMap(rulesByThLevel.get)
      .map(_.map(rule => invokeRule(rule, village)))
      .map(AnalysisReport(village, _))
  }

  private def invokeRule(rule: Rule, village: Village): RuleInvocation = {
    TimedInvocation.run(() => rule.analyse(village)) match {
      case (result: RuleResult, duration: Duration) => RuleInvocation(result, duration)
    }
  }
}
