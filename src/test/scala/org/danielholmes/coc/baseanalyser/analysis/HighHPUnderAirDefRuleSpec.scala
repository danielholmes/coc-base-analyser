package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class HighHPUnderAirDefRuleSpec extends FlatSpec with Matchers {
  val rule = new HighHPUnderAirDefRule

  "HighHPUnderAirDefRule" should "return no violation for base without air def" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return pass for base with air def and no storages" in {
    rule.analyse(Village(Set(AirDefense(1, TileCoordinate.Middle)))).success should be (true)
  }

  it should "return fail for base with air def and storage outside" in {
    val storage = GoldStorage(1, TileCoordinate.Origin)
    rule.analyse(Village(Set(AirDefense(1, TileCoordinate.Middle), storage))) should be (HighHPUnderAirDefResult("HighHPUnderAirDef", Set(storage)))
  }

  it should "return fail for base with air def cutting storage" in {
    val storageOutside = GoldStorage(1, TileCoordinate(0, 9))
    val storageInside = GoldStorage(1, TileCoordinate(3, 3))
    rule.analyse(Village(Set(AirDefense(1, TileCoordinate.Origin), storageOutside, storageInside))) should be (HighHPUnderAirDefResult("HighHPUnderAirDef", Set(storageOutside)))
  }

  it should "return true for base with air def just covering storage" in {
    rule.analyse(Village(Set(AirDefense(1, TileCoordinate.Origin), GoldStorage(1, TileCoordinate(0, 5))))).success should be (true)
  }
}