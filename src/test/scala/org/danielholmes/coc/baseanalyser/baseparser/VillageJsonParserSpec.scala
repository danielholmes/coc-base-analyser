package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model.{Base, TileSize, TileCoordinate, Element}
import org.scalatest._

class VillageJsonParserSpec extends FlatSpec with Matchers {
  val buildingFactory = StubElementFactory$

  val parser = new VillageJsonParser(buildingFactory)

  "A Village JSON Parser" should "throw an exception if invalid json provided" in {
    a [InvalidJsonException] should be thrownBy {
      parser.parse("something random[ {")
    }
  }

  it should "return empty village is empty input" in {
    parser.parse("[]") should be (Base.empty)
  }

  it should "return simple village" in {
    val result = parser.parse("""[{ "data": 1000001, "lvl": 1, "x": 21, "y": 20 }]""")

    result should be (Base(Set(new StubBaseElement(1, new TileCoordinate(21, 20)))))
  }

  it should "return village without ignored elements" in {
    val result = parser.parse("""[{ "data": 999999, "lvl": 1, "x": 21, "y": 20 }]""")

    result should be (Base.empty)
  }
}

object StubElementFactory$ extends ElementFactory {
  def build(raw: RawElement): Option[Element] = {
    Some(raw)
      .filter(value => value.data != 999999)
      .map(value => StubBaseElement(value.lvl, new TileCoordinate(value.x, value.y)))
  }
}

case class StubBaseElement(level: Int, coordinate: TileCoordinate) extends Element {
  val size: TileSize = new TileSize(3)
}