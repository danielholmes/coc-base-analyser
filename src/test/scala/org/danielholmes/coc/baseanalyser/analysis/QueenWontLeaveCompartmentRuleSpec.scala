package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.{AirDefense, _}
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class QueenWontLeaveCompartmentRuleSpec extends FlatSpec with Matchers {
  val rule = new QueenWontLeaveCompartmentRule

  "QueenWontLeaveCompartmentRule" should "return no violation for base without element" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return no violation for base with walls but without queen" in {
    val walls = ElementsBuilder.fence(Tile(10, 10), 5, 5)
    rule.analyse(Village(walls)).success should be (true)
  }

  it should "return violation for base with queen but no compartments" in {
    rule.analyse(Village(Set(ArcherQueen(1, Tile(1, 1))))).success should be (false)
  }

  it should "return no violation for base with queen inside 9x9" in {
    val aq = ArcherQueen(1, Tile(13, 13))
    val walls = ElementsBuilder.fence(Tile(9, 9), 11, 11)
    rule.analyse(Village(walls + aq)).success should be (true)
  }

  it should "return violation for base with queen inside 7x7" in {
    val aq = ArcherQueen(1, Tile(13, 13))
    val walls = ElementsBuilder.fence(Tile(10, 10), 9, 9)
    rule.analyse(Village(walls + aq)).success should be (false)
  }
}