package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._
import org.scalactic.anyvals.{PosInt, PosZInt}

class VillageJsonParserSpec extends FlatSpec with Matchers {
  val buildingFactory = StubElementFactory

  val parser = new VillageJsonParser(buildingFactory)

  "A Village JSON Parser" should "throw an exception if invalid json provided" in {
    a [InvalidJsonException] should be thrownBy {
      parser.parse("something random[ {")
    }
  }

  it should "return empty village is empty input" in {
    val villages = parser.parse("""{"war_base": false, "buildings":[]}""")

    villages should be (Villages(Village.empty, None))
  }

  it should "return simple village" in {
    val result = parser.parse("""{"exp_ver":1, "war_base": false, "buildings":[{ "data": 1000001, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Villages(Village(Set(new StubBaseElement(1, Tile(21, 20)))), None))
  }

  it should "return village without ignored elements" in {
    val result = parser.parse("""{"exp_ver":1, "war_base": false, "buildings":[{ "data": 999999, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Villages(Village.empty, None))
  }

  it should "return war village" in {
    val result = parser.parse("""{"exp_ver":1, "war_layout": 4,"war_base": true, "buildings":[{ "data": 1000001, "lvl": 1, "x": 20, "y": 20, "l4x": 30, "l4y": 30 }]}""")

    result should be (Villages(
      Village(Set(new StubBaseElement(1, Tile(20, 20)))),
      Some(Village(Set(new StubBaseElement(1, Tile(30, 30)))))
    ))
  }
}

object StubElementFactory extends ElementFactory {
  def build(raw: RawElement): Option[Element] = {
    Some(raw)
      .filter(_.data != 999999)
      .map(r => StubBaseElement(PosInt.from(r.lvl).get, Tile(PosZInt.from(r.x).get, PosZInt.from(r.y).get)))
  }
}

case class StubBaseElement(level: PosInt, tile: Tile) extends Element {
  val size: PosInt = 3
}