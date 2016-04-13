package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{HogTargeting, HogRider}

class HogCCLureRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val clanCastleRadius = village.clanCastle
      .map(_.range)
    if (clanCastleRadius.isEmpty) return HogCCLureRuleResult(Set.empty)

    HogCCLureRuleResult(
      village.coordinatesAllowedToDropTroop
        .flatMap(HogRider.findTargets(_, village))
        .filter(_.cutsRadius(clanCastleRadius.get))
    )
  }
}

case class HogCCLureRuleResult(targeting: Set[HogTargeting]) extends RuleResult {
  val success = targeting.isEmpty
}
