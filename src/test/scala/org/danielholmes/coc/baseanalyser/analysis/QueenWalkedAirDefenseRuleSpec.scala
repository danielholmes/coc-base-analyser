package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class QueenWalkedAirDefenseRuleSpec extends FlatSpec with Matchers {
  val rule = new QueenWalkedAirDefenseRule

  "QueenWalkedAirDefenseRuleSpec" should "return no violation for empty village" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return violation for non-walled air defese" in {
    val ad = AirDefense(1, Tile(1, 1))
    val result = rule.analyse(Village(Set(ad))).asInstanceOf[QueenWalkedAirDefenseRuleResult]
    result.success should be (false)
    result.attackings.head.targeting should be (ad)
    result.nonReachableAirDefs should be (empty)
  }

  it should "return success for deep-walled air defense" in {
    val ad = AirDefense(1, Tile(15, 15))
    val elements: Set[Element] = ElementsBuilder.rectangle[Element](Tile(10, 10), 13, 13, 1, Wall(1, _)) ++
      ElementsBuilder.rectangle(Tile(12, 12), 3, 3, 3, Barrack(1, _)) ++
      Set(ad)
    val result = rule.analyse(Village(elements)).asInstanceOf[QueenWalkedAirDefenseRuleResult]
    result.success should be (true)
    result.attackings should be (empty)
    result.nonReachableAirDefs should contain (ad)
  }

  it should "return fail for shallow-walled air defense" in {
    val ad = AirDefense(1, Tile(15, 15))
    val elements: Set[Element] = ElementsBuilder.rectangle[Element](Tile(11, 11), 11, 11, 1, Wall(1, _)) ++
      ElementsBuilder.rectangle(Tile(12, 12), 3, 3, 3, Barrack(1, _)) ++
      Set(ad)
    val result = rule.analyse(Village(elements)).asInstanceOf[QueenWalkedAirDefenseRuleResult]
    result.success should be (false)
    result.attackings.head.targeting should be (ad)
    result.nonReachableAirDefs should be (empty)
  }
}