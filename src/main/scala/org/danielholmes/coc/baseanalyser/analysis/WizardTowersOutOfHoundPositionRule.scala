package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{LavaHound, Minion, MinionAttackPosition, WizardTowerHoundTargeting}
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.{AirDefense, WizardTower}

class WizardTowersOutOfHoundPositionsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val wts = village.elements
        .filter(_.isInstanceOf[WizardTower])
        .map(_.asInstanceOf[WizardTower])

    val wtInRange = wts.map(wt => (wt, village.airDefenses.filter(ad => wt.range.touches(LavaHound.getRestingArea(ad)))))
      .filter(_._2.nonEmpty)
      .flatMap(pair => pair._2.map(ad => WizardTowerHoundTargeting(pair._1, ad, LavaHound.getRestingArea(ad))))

    val outOfRange = wts.filterNot(wt => wtInRange.exists(_.tower == wt))

    WizardTowersOutOfHoundPositionsRuleResult(outOfRange, wtInRange)
  }
}

case class WizardTowersOutOfHoundPositionsRuleResult(
  outOfRange: Set[WizardTower],
  inRange: Set[WizardTowerHoundTargeting]
) extends RuleResult {
  val success = inRange.map(_.tower).size <= outOfRange.size
  val ruleDetails = WizardTowersOutOfHoundPositionsRule.Details
}

object WizardTowersOutOfHoundPositionsRule {
  val Details = RuleDetails(
    "WizardTowersOutOfHoundPositions",
    "WTs avoid hounds",
    "Enough Wizard Towers out of hound range",
    """Wizard Towers are strong against loons, they shouldn't be too close to air defenses where hounds can tank for them.
      |You should have at least 2 that wont target resting hounds""".stripMargin
  )
}
