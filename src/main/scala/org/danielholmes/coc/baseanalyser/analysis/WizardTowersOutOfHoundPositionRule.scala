package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition, WizardTowerHoundTargeting}
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.{AirDefense, WizardTower}

class WizardTowersOutOfHoundPositionsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val wts = village.elements
        .filter(_.isInstanceOf[WizardTower])
        .map(_.asInstanceOf[WizardTower])

    // TODO: Introduce hound object
    val wtInRange = wts.map(wt => (wt, village.airDefenses.filter(ad => wt.range.touches(ad.block))))
      .filter(_._2.nonEmpty)
      .flatMap(pair => pair._2.map(WizardTowerHoundTargeting(pair._1, _)))

    val outOfRange = wts.filterNot(wt => wtInRange.exists(_.tower == wt))

    WizardTowersOutOfHoundPositionsRuleResult(
      outOfRange,
      wtInRange,
      village.airDefenses
    )
  }
}

case class WizardTowersOutOfHoundPositionsRuleResult(outOfRange: Set[WizardTower], inRange: Set[WizardTowerHoundTargeting], houndPositions: Set[AirDefense]) extends RuleResult {
  val success = inRange.size <= outOfRange.size
  val ruleDetails = WizardTowersOutOfHoundPositionsRule.Details
}

object WizardTowersOutOfHoundPositionsRule {
  val Details = RuleDetails(
    "WizardTowersOutOfHoundPositions",
    "WTs avoid hounds",
    "Enough Wizard Towers out of hound range",
    "Wizard Towers are strong against loons, they shouldn't be too close to air defenses where hounds can tank for them. You should have at least 2 that wont target resting hounds"
  )
}
