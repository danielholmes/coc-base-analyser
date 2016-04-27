package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalactic.anyvals.{PosInt, PosZInt}
import org.scalatest._

class MinimumCompartmentsRuleSpec extends FlatSpec with Matchers {
  val rule = new MinimumCompartmentsRule

  "MinimumCompartmentsRule" should "return violation for empty village" in {
    rule.analyse(Village.empty) should be (MinimumCompartmentsRuleResult(8, Set.empty))
  }

  it should "return violation for 1 compartment" in {
    rule.analyse(Village(ElementsBuilder.fence(Tile(1, 1), 3, 3))) should be
      (MinimumCompartmentsRuleResult(8, Set.empty))
  }

  it should "return no violation for 8 compartments" in {
    val elements = Range(0, 8)
      .map(x => PosZInt.from(x * 4).get)
      .map(Tile.MapOrigin.offset(_, 1))
      .flatMap(t => ElementsBuilder.fence(t, 4, 4) + BuilderHut(t.offset(1, 1)))
      .toSet
    rule.analyse(Village(elements)).success should be (true)
  }

  it should "return violation for 8 empty compartments" in {
    val elements = Range.inclusive(1, 22, 3)
      .map(PosInt.from(_).get)
      .flatMap(x => ElementsBuilder.fence(Tile(x, 1), 3, 3))
      .toSet
    rule.analyse(Village(elements)).success should be (false)
  }
}