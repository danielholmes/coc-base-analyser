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
    val elements =
      ElementsBuilder.fence(Tile(9, 9), 27, 27) ++
      ElementsBuilder.repeatX(Tile(10, 10), 5, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(10, 15), 5, 5, ArmyCamp(1, _)) ++
      Set[Element](
        ArmyCamp(1, Tile(10, 20)), ArmyCamp(1, Tile(15, 20)), BarbarianKing(1, Tile(21, 21)), ArmyCamp(1, Tile(25, 20)), ArmyCamp(1, Tile(30, 20))
      ) ++
      ElementsBuilder.repeatX(Tile(10, 25), 5, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(10, 30), 5, 5, ArmyCamp(1, _))
    rule.analyse(Village(elements)).success should be (true)
  }

  it should "return fail for exposed BK" in {
    val elements =
      ElementsBuilder.fence(Tile(9, 9), 27, 27) ++
      ElementsBuilder.repeatX(Tile(10, 10), 5, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(10, 15), 5, 5, ArmyCamp(1, _)) ++
      Set[Element](
        ArmyCamp(1, Tile(10, 20)), ArmyCamp(1, Tile(15, 20)), ArmyCamp(1, Tile(20, 20)), Barrack(1, Tile(26, 21)),
        BarbarianKing(1, Tile(29, 21)), Barrack(1, Tile(32, 21))
      ) ++
      ElementsBuilder.repeatX(Tile(10, 25), 5, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(10, 30), 5, 5, ArmyCamp(1, _))

    val result = rule.analyse(Village(elements))
    result.success should be (false)
  }

  it should "return success for very deep non-walled BK" in {
    val elements =
      ElementsBuilder.repeatX(Tile(5, 5), 7, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(5, 10), 7, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(5, 15), 7, 5, ArmyCamp(1, _)) ++
      Set[Element](
        ArmyCamp(1, Tile(5, 20)), ArmyCamp(1, Tile(10, 20)), ArmyCamp(1, Tile(15, 20)),
        BarbarianKing(1, Tile(21, 21)),
        ArmyCamp(1, Tile(25, 20)), ArmyCamp(1, Tile(30, 20)), ArmyCamp(1, Tile(35, 20))
      ) ++
      ElementsBuilder.repeatX(Tile(5, 25), 7, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(5, 30), 7, 5, ArmyCamp(1, _)) ++
      ElementsBuilder.repeatX(Tile(5, 35), 7, 5, ArmyCamp(1, _))
    val result = rule.analyse(Village(elements))
    result.success should be (true)
  }

  it should "return success for only slightly exposed BK" in {
    val elements =
      ElementsBuilder.fence(Tile(4, 4), 15, 15) ++
        ElementsBuilder.repeatX(Tile(6, 6), 3, 4, Barrack(1, _)) ++
        Set[Element](
          Barrack(1, Tile(6, 10)),
          BarbarianKing(1, Tile(10, 10)),
          Barrack(1, Tile(14, 10))
        ) ++
        ElementsBuilder.repeatX(Tile(6, 14), 3, 4, Barrack(1, _))
    val result = rule.analyse(Village(elements))
    result.success should be (true)
  }

  it should "return correct exposed tiles for offset BK" in {
    val elements =
      ElementsBuilder.fence(Tile(4, 4), 14, 15) ++
        ElementsBuilder.repeatX(Tile(6, 6), 3, 4, Barrack(1, _)) ++
        Set[Element](
          Barrack(1, Tile(6, 10)),
          BarbarianKing(1, Tile(10, 10)),
          Barrack(1, Tile(14, 10))
        ) ++
        ElementsBuilder.repeatX(Tile(6, 14), 3, 4, Barrack(1, _))

    val result = rule.analyse(Village(elements))
    result.success should be (false)
    result.asInstanceOf[BKSwappableRuleResult].exposedTiles should contain (Tile(18, 11))
    result.asInstanceOf[BKSwappableRuleResult].exposedTiles should not contain (Tile(3, 11))
  }
}
