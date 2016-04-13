package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class WizardTowersOutOfHoundPositionsRuleSpec extends FlatSpec with Matchers {
  val rule = new WizardTowersOutOfHoundPositionsRule

  "WizardTowersOutOfHoundPositionsRule" should "return success for no air defs" in {
    val wt = WizardTower(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(wt)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set(wt), Set.empty, Set.empty))
  }

  it should "return success for no wiz towers" in {
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set.empty, Set.empty, Set(ad)))
  }

  it should "return fail for wt in range of air def" in {
    val wt = WizardTower(1, Tile(4, 4))
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad, wt)))
    result.success should be (false)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set.empty, Set(wt), Set(ad)))
  }

  it should "return succeed for wt out of range of air def" in {
    val wt = WizardTower(1, Tile(20, 4))
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad, wt)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set(wt), Set.empty, Set(ad)))
  }
}