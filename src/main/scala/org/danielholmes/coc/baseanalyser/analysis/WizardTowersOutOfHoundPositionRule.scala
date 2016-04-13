package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model._

class WizardTowersOutOfHoundPositionsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val wts = village.elements
        .filter(_.isInstanceOf[WizardTower])
        .map(_.asInstanceOf[WizardTower])

    val airDefs = village.elements
      .filter(_.isInstanceOf[AirDefense])
      .map(_.asInstanceOf[AirDefense])

    // TODO: Introduce hound object
    val wtInRange = wts.partition(wt => airDefs.exists(ad => wt.range.touches(ad.block)))

    WizardTowersOutOfHoundPositionsRuleResult(
      wtInRange._2,
      wtInRange._1,
      airDefs
    )
  }
}

case class WizardTowersOutOfHoundPositionsRuleResult(outOfRange: Set[WizardTower], inRange: Set[WizardTower], houndPositions: Set[AirDefense]) extends RuleResult {
  val success = inRange.isEmpty
}
