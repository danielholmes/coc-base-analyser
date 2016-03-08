package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._

class HogCCLureRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val clanCastleRadius = village.clanCastle
      .map(_.range)
    if (clanCastleRadius.isEmpty) return RuleResult.pass(name)

    HogCCLureResult(
      name,
      village.attackPlacementCoordinates
        .flatMap(HogRider.findTarget(_, village))
        .filter(_.cutsRadius(clanCastleRadius.get))
    )
  }

  private val name: String = "HogCCLure"
}

case class HogCCLureResult(ruleName: String, targeting: Set[HogTargeting]) extends RuleResult {
  val success = targeting.isEmpty
}
