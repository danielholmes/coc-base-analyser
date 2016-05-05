package org.danielholmes.coc.baseanalyser.model.range

import org.danielholmes.coc.baseanalyser.model.{Angle, MapCoordinate, TileCoordinate}
import org.scalatest._

class BlindSpotSectorElementRangeSpec extends FlatSpec with Matchers {
  "BlindSpotSectorElementRange" should "contains inside" in {
    BlindSpotSectorElementRange(TileCoordinate(10, 10), 1.0, 10.0, Angle.Quarter, Angle.Half).contains(MapCoordinate(15.0, 10.0)) should be (true)
  }

  it should "not contain outside" in {
    BlindSpotSectorElementRange(TileCoordinate(10, 10), 1.0, 10.0, Angle.Quarter, Angle.Half).contains(MapCoordinate(25.0, 10.0)) should be (false)
  }

  it should "not contain outside opposite direction" in {
    BlindSpotSectorElementRange(TileCoordinate(10, 10), 1.0, 10.0, Angle.Quarter, Angle.Half).contains(MapCoordinate(5.0, 10.0)) should be (false)
  }
}
