package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class MinimumCompartmentsRuleSpec extends FlatSpec with Matchers {
  val rule = new MinimumCompartmentsRule

  "MinimumCompartmentsRule" should "return violation for empty village" in {
    rule.analyse(Village.empty) should be (MinimumCompartmentsRuleResult(8, Set.empty))
  }

  it should "return violation for 1 compartment" in {
    val walls = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(1, 1), Wall(1, _))
    rule.analyse(Village(walls.map(_.asInstanceOf[Element]))) should be (MinimumCompartmentsRuleResult(8, Set(WallCompartment(walls, Set(Tile(2, 2))))))
  }

  it should "return no violation for 9 compartments" in {
    val walls = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(1, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(4, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(7, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(10, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(13, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(16, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(19, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(22, 1), Wall(1, _)) ++
      ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(25, 1), Wall(1, _))
    rule.analyse(Village(walls.map(_.asInstanceOf[Element]))).success should be (true)
  }
}