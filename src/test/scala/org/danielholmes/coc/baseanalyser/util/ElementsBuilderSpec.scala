package org.danielholmes.coc.baseanalyser.util

import org.danielholmes.coc.baseanalyser.model.{Tile, Wall}
import org.scalatest._

class ElementsBuilderSpec extends FlatSpec with Matchers {
  "ElementsBuilder" should "return correctly built elements" in {
    ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(10, 10), Wall(1, _)) should be (Set(
      Wall(1, Tile(10, 10)), Wall(1, Tile(11, 10)), Wall(1, Tile(12, 10)),
      Wall(1, Tile(10, 11)), /*Wall(1, Tile(11, 11)),*/ Wall(1, Tile(12, 11)),
      Wall(1, Tile(10, 12)), Wall(1, Tile(11, 12)), Wall(1, Tile(12, 12))
    ))
  }
}