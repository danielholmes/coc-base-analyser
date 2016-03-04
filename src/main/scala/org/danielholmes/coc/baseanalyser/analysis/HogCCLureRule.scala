package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._

class HogCCLureRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val clanCastleRadius = village.clanCastle
      .map(_.radius)
    if (clanCastleRadius.isEmpty) return RuleResult.pass

    HogCCLureFail(
      village.attackPlacementCoordinates
        .flatMap(HogRider.findTarget(_, village))
        .filter(_.cutsRadius(clanCastleRadius.get))
    )
  }
}

case class HogCCLureFail(targeting: Set[HogTargeting]) extends RuleResult {
  val success = targeting.isEmpty
}
