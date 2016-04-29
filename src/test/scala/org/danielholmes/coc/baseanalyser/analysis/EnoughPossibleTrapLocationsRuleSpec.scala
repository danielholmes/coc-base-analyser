package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalactic.anyvals.{PosZDouble, PosZInt}
import org.scalatest._

class EnoughPossibleTrapLocationsRuleSpec extends FlatSpec with Matchers {
  val rule = new EnoughPossibleTrapLocationsRule

  "EnoughPossibleTrapLocationsRule" should "return violation for base without wall compartments" in {
    rule.analyse(Village.empty).success should be (false)
  }

  it should "return no violation for base with large wall compartments" in {
    val walls = ElementsBuilder.elementFence(Tile.MapOrigin, 40, 40)
    rule.analyse(Village(walls)).success should be (true)
  }

  it should "return violation if only 20 possibilities" in {
    val walls = Range.inclusive(1, 4)
      .flatMap(row =>
        Range.inclusive(1, 5)
          .map(col => Tile(PosZInt.from(col * 4).get, PosZInt.from(row * 4).get))
          .flatMap(ElementsBuilder.elementFence(_, 4, 4))
      )
      .toSet
    val village = Village(walls)
    assert(village.possibleInternalLargeTraps.size == 20)

    rule.analyse(village).success should be (false)
  }

  it should "return no violation if 24 possibilities" in {
    val walls = Range.inclusive(1, 4)
      .flatMap(row =>
        Range.inclusive(1, 6)
          .map(col => Tile(PosZInt.from(col * 4).get, PosZInt.from(row * 4).get))
          .flatMap(ElementsBuilder.elementFence(_, 4, 4))
      )
      .toSet
    val village = Village(walls)
    assert(village.possibleInternalLargeTraps.size == 24)

    rule.analyse(village).success should be (true)
  }

  it should "allocate an equal score for 2 separate 2x2s as 1 3x3" in {
    val villageSeparate2x2 = Village(ElementsBuilder.elementFence(Tile(10, 10), 4, 4) ++ ElementsBuilder.wallFence(Tile(15, 15), 4, 4))
    val village3x3 = Village(ElementsBuilder.elementFence(Tile(10, 10), 5, 5))

    rule.calculateScore(village3x3) should be (rule.calculateScore(villageSeparate2x2))
  }

  it should "give a higher score for 2 separate 2x2s then 1 3x2" in {
    val villageSeparate2x2 = Village(ElementsBuilder.elementFence(Tile(10, 10), 4, 4) ++ ElementsBuilder.wallFence(Tile(15, 15), 4, 4))
    val village3x2 = Village(ElementsBuilder.elementFence(Tile(10, 10), 5, 4))

    rule.calculateScore(village3x2) should be < rule.calculateScore(villageSeparate2x2)
  }

  it should "single trap should score 1.0" in {
     rule.calculateScore(Village(ElementsBuilder.elementFence(Tile(10, 10), 4, 4))) should be (PosZDouble(1.0))
  }
}
