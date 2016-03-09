package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class MapTileCoordinateSpec extends FlatSpec with Matchers {
  val coord = MapTileCoordinate(5, 5)

  "Tile Coordinate" should "find correct x axis right distance" in {
    coord.distanceTo(MapTileCoordinate(10, 5)) should be (5)
  }

  it should "find correct diagonal distance" in {
    coord.distanceTo(MapTileCoordinate(8, 9)) should be (5)
  }
}