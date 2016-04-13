package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.MinionAttackPosition
import org.scalatest._

class AirSnipedDefenseRuleSpec extends FlatSpec with Matchers {
  val rule = new AirSnipedDefenseRule

  "AirSnipedDefenseRule" should "return no violation for base without defenses" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return sniped ground when no air def" in {
    val cannon = Cannon(1, Tile.MapOrigin)
    rule.analyse(Village(Set(cannon))) should be (AirSnipedDefenseRuleResult(
      cannon.block.expandBy(TileSize(1)).allCoordinates.map(MinionAttackPosition(_, cannon)),
      Set.empty
    ))
  }

  it should "return no sniped ground when air def" in {
    val airDef = AirDefense(1, Tile(4, 4))
    rule.analyse(Village(Set(Cannon(1, Tile.MapOrigin), airDef))) should be (AirSnipedDefenseRuleResult(
      Set.empty,
      Set(airDef)
    ))
  }
}