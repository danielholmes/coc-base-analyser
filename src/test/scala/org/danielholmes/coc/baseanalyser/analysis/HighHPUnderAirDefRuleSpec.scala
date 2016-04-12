package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class HighHPUnderAirDefRuleSpec extends FlatSpec with Matchers {
  val rule = new HighHPUnderAirDefRule

  "HighHPUnderAirDefRule" should "return no violation for base without air def" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return pass for base with air def and no storages" in {
    rule.analyse(Village(Set(AirDefense(1, Tile.MapOrigin)))).success should be (true)
  }

  it should "return fail for base with air def and storage outside" in {
    val storage = GoldStorage(1, Tile(30, 30))
    rule.analyse(Village(Set(AirDefense(1, Tile.MapOrigin), storage))) should be (HighHPUnderAirDefResult(Set(storage), Set.empty))
  }

  it should "return fail for base with air def cutting storage" in {
    val storageOutside = GoldStorage(1, Tile(5, 9))
    val storageInside = GoldStorage(1, Tile(5, 3))
    rule.analyse(Village(Set(AirDefense(1, Tile.MapOrigin), storageOutside, storageInside))) should be (HighHPUnderAirDefResult(Set(storageOutside), Set(storageInside)))
  }

  it should "return true for base with air def just covering storage" in {
    rule.analyse(Village(Set(AirDefense(1, Tile.MapOrigin), GoldStorage(1, Tile(3, 5))))).success should be (true)
  }

  it should "return success for base with storage requiring 2 air defs for full coverage" in {
    val village = Village(Set(
      AirDefense(1, Tile(5, 5)),
      GoldStorage(1, Tile(14, 5)),
      AirDefense(1, Tile(23, 5))
    ))
    rule.analyse(village).success should be (true)
  }
}