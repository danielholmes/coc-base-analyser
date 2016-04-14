package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.model.troops.HogTargeting
import org.scalatest._

class HogTargetingSpec extends FlatSpec with Matchers {
  "HogTargeting" should "return correct cutting result for non-cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(1, Tile(10, 0)))
      .cutsRadius(ElementRange(MapCoordinate(5, 5), 1)) shouldBe false
  }

  it should "return correct cutting result for just cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(1, Tile(10, 0)))
      .cutsRadius(ElementRange(MapCoordinate(5, 2), 2)) shouldBe false
  }

  it should "return correct cutting result for middle cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(1, Tile(10, 0)))
      .cutsRadius(ElementRange(MapCoordinate(5, 1), 2)) shouldBe true
  }

  it should "return correct cutting result for random" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(1, Tile(0, 5)))
      .cutsRadius(ElementRange(MapCoordinate(0, 10), 1)) shouldBe false
  }
}