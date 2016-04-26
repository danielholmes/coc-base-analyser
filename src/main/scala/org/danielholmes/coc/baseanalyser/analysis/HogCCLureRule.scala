package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{HogTargeting, HogRider}

class HogCCLureRule extends Rule {
  def analyse(village: Village): RuleResult = {
    village.clanCastle
      .map(_.range)
      .map(range =>
        village.coordinatesAllowedToDropTroop
          .flatMap(HogRider.findTargets(_, village))
          .filter(_.cutsRadius(range))
      )
      .map(HogCCLureRuleResult)
      .getOrElse(HogCCLureRuleResult(Set.empty))
  }
}

case class HogCCLureRuleResult(targeting: Set[HogTargeting]) extends RuleResult {
  val success = targeting.isEmpty
}
