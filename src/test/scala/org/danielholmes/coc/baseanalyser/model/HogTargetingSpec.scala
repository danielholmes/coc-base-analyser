package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.trash.BuilderHut
import org.danielholmes.coc.baseanalyser.model.troops.HogTargeting
import org.scalatest._

class HogTargetingSpec extends FlatSpec with Matchers {
  "HogTargeting" should "return correct cutting result for non-cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(Tile(10, 0)))
      .cutsRadius(CircularElementRange(FloatMapCoordinate(5, 5), 1)) shouldBe false
  }

  it should "return correct cutting result for just cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(Tile(10, 0)))
      .cutsRadius(CircularElementRange(FloatMapCoordinate(5, 2), 2)) shouldBe false
  }

  it should "return correct cutting result for middle cutting" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(Tile(10, 0)))
      .cutsRadius(CircularElementRange(FloatMapCoordinate(5, 1), 2)) shouldBe true
  }

  it should "return correct cutting result for random" in {
    HogTargeting(TileCoordinate(0, 0), BuilderHut(Tile(0, 5)))
      .cutsRadius(CircularElementRange(FloatMapCoordinate(0, 10), 1)) shouldBe false
  }
}
