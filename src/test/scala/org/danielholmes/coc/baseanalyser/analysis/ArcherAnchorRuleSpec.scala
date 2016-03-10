package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class ArcherAnchorRuleSpec extends FlatSpec with Matchers {
  val rule = new ArcherAnchorRule

  "ArcherAnchorRule" should "return no violation for base without element" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return violation for base with element and no defenses" in {
    rule.analyse(Village(Set(BuilderHut(1, Tile.MapOrigin)))).success should be (false)
  }

  it should "return success for base with element covered by ground shooting" in {
    val village = Village(
      Set(
        BuilderHut(1, Tile(5, 5)),
        ArcherTower(1, Tile(7, 7))
      )
    )
    rule.analyse(village).success should be (true)
  }

  it should "return fail for base with element covered by air shooting" in {
    val village = Village(
      Set(
        BuilderHut(1, Tile(5, 5)),
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
}