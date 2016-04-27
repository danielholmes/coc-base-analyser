package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class HogCCLureRuleSpec extends FlatSpec with Matchers {
  val rule = new HogCCLureRule

  "HogCCLureRule" should "return no violation for base without CC" in {
    rule.analyse(Village.empty).success should be (true)
  }

  it should "return violation for base with CC and no blocking" in {
    rule.analyse(Village(Set(ClanCastle(1, Tile.MapOrigin)))).success should be (false)
  }

  it should "return success for base with paths blocked off" in {
    val ccPosition = Tile(20, 20)
    val cc = ClanCastle(1, ccPosition)
    val village = Village(
      Set(cc) ++
        Tile(2, 2).matrixOfTilesTo(Tile(41, 41), 3)
          .filter(_ != ccPosition)
          .map(Barrack(1, _))
    )
    rule.analyse(village).success should be (true)
  }
}
