package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition, WizardTowerHoundTargeting}
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
    val wtInRange = wts.map(wt => (wt, airDefs.filter(ad => wt.range.touches(ad.block))))
      .filter(_._2.nonEmpty)
      .flatMap(pair => pair._2.map(WizardTowerHoundTargeting(pair._1, _)))

    val outOfRange = wts.filterNot(wt => wtInRange.exists(_.tower == wt))

    WizardTowersOutOfHoundPositionsRuleResult(
      outOfRange,
      wtInRange,
      airDefs
    )
  }
}

case class WizardTowersOutOfHoundPositionsRuleResult(outOfRange: Set[WizardTower], inRange: Set[WizardTowerHoundTargeting], houndPositions: Set[AirDefense]) extends RuleResult {
  val success = inRange.size <= outOfRange.size
}
