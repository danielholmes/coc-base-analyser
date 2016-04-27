package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalactic.anyvals.PosInt
import org.scalatest._

class MinimumCompartmentsRuleSpec extends FlatSpec with Matchers {
  val rule = new MinimumCompartmentsRule

  "MinimumCompartmentsRule" should "return violation for empty village" in {
    rule.analyse(Village.empty) should be (MinimumCompartmentsRuleResult(8, Set.empty))
  }

  it should "return violation for 1 compartment" in {
    val walls = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(1, 1), Wall(1, _))
    rule.analyse(Village(walls.map(_.asInstanceOf[Element]))) should be (MinimumCompartmentsRuleResult(8, Set(WallCompartment(walls, Set(Tile(2, 2)), Set.empty))))
  }

  it should "return no violation for 8 compartments" in {
    val elements = Range.inclusive(1, 22, 3)
      .map(PosInt.from(_).get)
      .flatMap(x => ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(x, 1), Wall(1, _)))
      .map(_.asInstanceOf[Element])
      .toSet
    rule.analyse(Village(elements)).success should be (true)
  }

  it should "return violation for 8 empty compartments" in {
    val elements = Range.inclusive(1, 22, 3)
      .map(PosInt.from(_).get)
      .flatMap(x => ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(x, 1), Wall(1, _)))
      .map(_.asInstanceOf[Element])
      .toSet
    rule.analyse(Village(elements)).success should be (false)
  }
}