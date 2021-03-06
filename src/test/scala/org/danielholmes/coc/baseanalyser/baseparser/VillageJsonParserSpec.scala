package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._
import org.scalactic.anyvals.{PosInt, PosZInt}

class VillageJsonParserSpec extends FlatSpec with Matchers {
  val buildingFactory = StubElementFactory

  val parser = new VillageJsonParser(buildingFactory)

  "A Village JSON Parser" should "throw an exception if invalid json provided" in {
    a[InvalidJsonException] should be thrownBy {
      parser.parse("something random[ {")
    }
  }

  it should "return empty village is empty input" in {
    val villages = parser.parse("""{"war_base": false, "buildings":[]}""")

    villages should be (Villages(Village.empty, None))
  }

  it should "return simple village" in {
    val result = parser.parse("""{"exp_ver":1, "war_base": false, "buildings":[{ "data": 1000001, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Villages(Village(Set(StubBaseElement(1, Tile(21, 20), None))), None))
  }

  it should "return village without ignored elements" in {
    val result = parser.parse("""{"exp_ver":1, "war_base": false, "buildings":[{ "data": 999999, "lvl": 1, "x": 21, "y": 20 }]}""")

    result should be (Villages(Village.empty, None))
  }

  it should "return war village" in {
    val result = parser.parse(
      """{"exp_ver":1, "war_layout": 4,"war_base": true,
        |"buildings":[{ "data": 1000001, "lvl": 1, "x": 20, "y": 20, "l4x": 30, "l4y": 30 }]}""".stripMargin)

    result should be (Villages(
      Village(Set(StubBaseElement(1, Tile(20, 20), None))),
      Some(Village(Set(StubBaseElement(1, Tile(30, 30), None))))
    ))
  }

  it should "return war element aim angle" in {
    val result = parser.parse(
      """{"exp_ver":1, "war_layout": 4,"war_base": true,
        |"buildings":[{ "data": 1000029, "lvl": 1, "x": 20, "y": 20, "l4x": 30, "l4y": 30, "aim_angle": 90, "aim_angle_war": 45 }]}""".stripMargin)

    result should be (Villages(
      Village(Set(StubBaseElement(1, Tile(20, 20), Some(90)))),
      Some(Village(Set(StubBaseElement(1, Tile(30, 30), Some(45)))))
    ))
  }

  it should "return war village without not yet placed buildings" in {
    val result = parser.parse(
      """{"exp_ver":1, "war_layout": 4,"war_base": true,
        |"buildings":[
        |{ "data": 1000001, "lvl": 1, "x": 20, "y": 20, "l4x": 30, "l4y": 30 },
        |{ "data": 1000001, "lvl": 2, "x": 40, "y": 40 }]}""".stripMargin)

    result should be (Villages(
      Village(Set(StubBaseElement(1, Tile(20, 20), None), StubBaseElement(2, Tile(40, 40), None))),
      Some(Village(Set(StubBaseElement(1, Tile(30, 30), None))))
    ))
  }
}

object StubElementFactory extends ElementFactory {
  def build(raw: RawElement): Option[Element] = {
    Some(raw)
      .filter(_.data != 999999)
      .map(r => StubBaseElement(PosInt.from(r.lvl).get, Tile(PosZInt.from(r.x).get, PosZInt.from(r.y).get), raw.aimAngle))
  }
}

case class StubBaseElement(level: PosInt, tile: Tile, aimAngle: Option[Int]) extends Element {
  val size = PosInt(3)
}
