package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.{AirDefense, WizardTower}
import org.danielholmes.coc.baseanalyser.model.troops.WizardTowerHoundTargeting
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class WizardTowersOutOfHoundPositionsRuleSpec extends FlatSpec with Matchers {
  val rule = new WizardTowersOutOfHoundPositionsRule

  "WizardTowersOutOfHoundPositionsRule" should "return success for no air defs" in {
    val wt = WizardTower(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(wt)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set(wt), Set.empty))
  }

  it should "return success for no wiz towers" in {
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set.empty, Set.empty))
  }

  it should "return fail for wt in range of air def" in {
    val wt = WizardTower(1, Tile(4, 4))
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad, wt)))
    result.success should be (false)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set.empty, Set(WizardTowerHoundTargeting(wt, ad, ad.block.contractBy(1)))))
  }

  it should "return succeed for wt out of range of air def" in {
    val wt = WizardTower(1, Tile(20, 4))
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad, wt)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set(wt), Set.empty))
  }

  it should "return success for half wts in range of air def" in {
    val wtInRange = WizardTower(1, Tile(4, 4))
    val wtOutRange = WizardTower(1, Tile(35, 35))
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad, wtInRange, wtOutRange)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(Set(wtOutRange), Set(WizardTowerHoundTargeting(wtInRange, ad, ad.block.contractBy(1)))))
  }

  it should "count a WT in range of 2 air defs only once" in {
    val wtInRange = WizardTower(1, Tile(4, 4))
    val wtOutRange = WizardTower(1, Tile(35, 35))
    val ad1 = AirDefense(1, Tile(1, 1))
    val ad2 = AirDefense(1, Tile(1, 4))
    val result = rule.analyse(Village(Set(ad1, ad2, wtInRange, wtOutRange)))
    result.success should be (true)
    result should be (WizardTowersOutOfHoundPositionsRuleResult(
      Set(wtOutRange),
      Set(WizardTowerHoundTargeting(wtInRange, ad1, ad1.block.contractBy(1)),
        WizardTowerHoundTargeting(wtInRange, ad2, ad2.block.contractBy(1))))
    )
  }
}
