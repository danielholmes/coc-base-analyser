package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.{TileCoordinate, ClanCastle, Village}
import org.scalatest._

class HogCCLureRuleSpec extends FlatSpec with Matchers {
  val rule = new HogCCLureRule

  "HogCCLureRule" should "return no violation for base without CC" in {
    rule.analyse(Village.empty).success should be (true)
  }

  "HogCCLureRule" should "return violation for base with CC and no blocking" in {
    rule.analyse(Village(Set(ClanCastle(1, TileCoordinate.Middle)))).success should be (false)
  }
}