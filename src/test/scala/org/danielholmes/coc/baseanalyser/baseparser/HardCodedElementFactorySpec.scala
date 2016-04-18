package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class HardCodedElementFactorySpec extends FlatSpec with Matchers {
  val factory = new HardCodedElementFactory

  "Hardcoded Building Factory" should "reject unknown code" in {
    a [RuntimeException] should be thrownBy {
      factory.build(new RawElement(-1, 9, 1, 2))
    }
  }

  it should "create town hall" in {
    factory.build(new RawElement(1000001, 9, 3, 3)) should contain (TownHall(10, Tile.MapOrigin))
  }

  it should "create construction buildings as level 1" in {
    factory.build(new RawElement(1000008, -1, 3, 4)) should contain (Cannon(1, Tile(1, 2)))
  }

  it should "ignore obstacles" in {
    factory.build(new RawElement(8000000, 9, 5, 6)) should be (None)
  }

  it should "ignore decorations" in {
    factory.build(new RawElement(18000000, 9, 5, 6)) should be (None)
  }
}