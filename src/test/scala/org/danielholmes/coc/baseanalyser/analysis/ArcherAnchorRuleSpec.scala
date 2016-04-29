package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.{AirDefense, ArcherTower, Cannon}
import org.danielholmes.coc.baseanalyser.model.trash.BuilderHut
import org.scalatest._

class ArcherAnchorRuleSpec extends FlatSpec with Matchers {
  val rule = new ArcherAnchorRule

  "ArcherAnchorRule" should "return no violation for base without element" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return violation for base with element and no defenses" in {
    rule.analyse(Village(Set(BuilderHut(Tile.MapOrigin)))).success should be (false)
  }

  it should "return success for base with element covered by ground shooting" in {
    val village = Village(
      Set(
        BuilderHut(Tile(5, 5)),
        ArcherTower(1, Tile(7, 7))
      )
    )
    rule.analyse(village).success should be (true)
  }

  it should "return fail for base with element covered by air shooting" in {
    val village = Village(
      Set(
        BuilderHut(Tile(5, 5)),
        AirDefense(1, Tile(7, 7))
      )
    )
    rule.analyse(village).success should be (false)
  }

  it should "return success for base with wall not covered by ground shooting" in {
    val village = Village(
      Set(
        Wall(1, Tile(5, 5)),
        ArcherTower(1, Tile(30, 30))
      )
    )
    rule.analyse(village).success should be (true)
  }

  it should "return defenses that can hit archer" in {
    val at = ArcherTower(1, Tile(30, 30))
    val cannon = Cannon(1, Tile(36, 36))
    val village = Village(Set(at, AirDefense(1, Tile(33, 33)), cannon))
    rule.analyse(village).asInstanceOf[ArcherAnchorRuleResult].aimingDefenses should be (Set(at, cannon))
  }
}
