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
    parser.parse("""{"buildings":[]}""") should be (Village.empty)
  }

  it should "return simple village" in {
    val result = parser.parse("""{"exp_ver":1,"buildings":[{ "data": 1000001, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Village(Set(new StubBaseElement(1, Tile(21, 20)))))
  }

  it should "return village without ignored elements" in {
    val result = parser.parse("""{"exp_ver":1,"buildings":[{ "data": 999999, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Village.empty)
  }
}

object StubElementFactory extends ElementFactory {
  def build(raw: RawBuilding): Option[Element] = {
    Some(raw)
      .filter(_.data != 999999)
      .map(r => StubBaseElement(PosInt.from(r.lvl).get, Tile(PosZInt.from(r.x).get, PosZInt.from(r.y).get)))
  }
}

case class StubBaseElement(level: PosInt, tile: Tile) extends Element {
  val size: PosInt = 3
}