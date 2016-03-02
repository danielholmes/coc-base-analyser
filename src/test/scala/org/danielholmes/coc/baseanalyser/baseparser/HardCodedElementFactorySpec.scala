package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model.{Base, TileCoordinate, TownHall}
import org.scalatest._

class HardCodedElementFactorySpec extends FlatSpec with Matchers {
  val factory = new HardCodedElementFactory()

  "Hardcoded Building Factory" should "reject unknown code" in {
    a [RuntimeException] should be thrownBy {
      factory.build(new RawElement(-1, 9, 1, 2))
    }
  }

  it should "create town hall" in {
    factory.build(new RawElement(1000001, 9, 1, 2)) should be (Some(new TownHall(9, new TileCoordinate(1, 2))))
  }

  it should "ignore obstacles" in {
    factory.build(new RawElement(8000000, 9, 1, 2)) should be (None)
  }

  it should "ignore decorations" in {
    factory.build(new RawElement(18000000, 9, 1, 2)) should be (None)
  }
}