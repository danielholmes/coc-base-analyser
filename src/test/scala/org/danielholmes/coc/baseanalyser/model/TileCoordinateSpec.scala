package org.danielholmes.coc.baseanalyser.model

import org.scalatest._
import org.scalactic.anyvals.PosZDouble

class TileCoordinateSpec extends FlatSpec with Matchers {
  val coord = TileCoordinate(5, 5)

  "Tile Coordinate" should "find correct x axis right distance" in {
    coord.distanceTo(TileCoordinate(10, 5)) should be (PosZDouble(5))
  }

  it should "find correct diagonal distance" in {
    coord.distanceTo(TileCoordinate(8, 9)) should be (PosZDouble(5))
  }
}