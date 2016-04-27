package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.scalatest._

class AirSnipedDefenseRuleSpec extends FlatSpec with Matchers {
  val rule = new AirSnipedDefenseRule

  "AirSnipedDefenseRule" should "return no violation for base without defenses" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return sniped ground when no air def" in {
    val cannon = Cannon(1, Tile.MapOrigin)
    val result = rule.analyse(Village(Set(cannon))).asInstanceOf[AirSnipedDefenseRuleResult]
    result.success should be (false)
    result.snipedDefenses.size should be (1)
    result.snipedDefenses.map(_.targeting) should be (Set(cannon))
  }

  it should "return no sniped ground when air def" in {
    val airDef = AirDefense(1, Tile(4, 4))
    rule.analyse(Village(Set(Cannon(1, Tile.MapOrigin), airDef))) should be (AirSnipedDefenseRuleResult(
      Set.empty,
      Set(airDef)
    ))
  }
}
