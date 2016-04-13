package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class BKSwappableRuleSpec extends FlatSpec with Matchers {
  val rule = new BKSwappableRule

  "BKSwappableRule" should "return success for empty village" in {
    rule.analyse(Village.empty) should be (BKSwappableRuleResult(Set.empty))
  }

  it should "return fail for BK on his own" in {
    rule.analyse(Village(Set(BarbarianKing(1, Tile(1, 1))))).success should be (false)
  }

  it should "return success for deep walled BK" in {
    val elements = ElementsBuilder.rectangle(Tile(9, 9), 27, 27, 1, Wall(1, _)) ++
      Set[Element](
        ArmyCamp(1, Tile(10, 10)), ArmyCamp(1, Tile(15, 10)), ArmyCamp(1, Tile(20, 10)), ArmyCamp(1, Tile(25, 10)), ArmyCamp(1, Tile(30, 10)),
        ArmyCamp(1, Tile(10, 15)), ArmyCamp(1, Tile(15, 15)), ArmyCamp(1, Tile(20, 15)), ArmyCamp(1, Tile(25, 15)), ArmyCamp(1, Tile(30, 15)),
        ArmyCamp(1, Tile(10, 20)), ArmyCamp(1, Tile(15, 20)), BarbarianKing(1, Tile(21, 21)), ArmyCamp(1, Tile(25, 20)), ArmyCamp(1, Tile(30, 20)),
        ArmyCamp(1, Tile(10, 25)), ArmyCamp(1, Tile(15, 25)), ArmyCamp(1, Tile(20, 25)), ArmyCamp(1, Tile(25, 25)), ArmyCamp(1, Tile(30, 25)),
        ArmyCamp(1, Tile(10, 30)), ArmyCamp(1, Tile(15, 30)), ArmyCamp(1, Tile(20, 30)), ArmyCamp(1, Tile(25, 30)), ArmyCamp(1, Tile(30, 30))
      )
    rule.analyse(Village(elements)).success should be (true)
  }

  it should "return fail for exposed BK" in {
    val elements = ElementsBuilder.rectangle(Tile(9, 9), 27, 27, 1, Wall(1, _)) ++
      Set[Element](
        ArmyCamp(1, Tile(10, 10)), ArmyCamp(1, Tile(15, 10)), ArmyCamp(1, Tile(20, 10)), ArmyCamp(1, Tile(25, 10)), ArmyCamp(1, Tile(30, 10)),
        ArmyCamp(1, Tile(10, 15)), ArmyCamp(1, Tile(15, 15)), ArmyCamp(1, Tile(20, 15)), ArmyCamp(1, Tile(25, 15)), ArmyCamp(1, Tile(30, 15)),
        ArmyCamp(1, Tile(10, 20)), ArmyCamp(1, Tile(15, 20)), ArmyCamp(1, Tile(20, 20)), Barrack(1, Tile(26, 21)), BarbarianKing(1, Tile(29, 21)), Barrack(1, Tile(32, 21)),
        ArmyCamp(1, Tile(10, 25)), ArmyCamp(1, Tile(15, 25)), ArmyCamp(1, Tile(20, 25)), ArmyCamp(1, Tile(25, 25)), ArmyCamp(1, Tile(30, 25)),
        ArmyCamp(1, Tile(10, 30)), ArmyCamp(1, Tile(15, 30)), ArmyCamp(1, Tile(20, 30)), ArmyCamp(1, Tile(25, 30)), ArmyCamp(1, Tile(30, 30))
      )
    val result = rule.analyse(Village(elements))
    result.success should be (false)
  }

  // TODO: Consider channels running through base
}