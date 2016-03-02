package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class TileCoordinateSpec extends FlatSpec with Matchers {
  val coord = TileCoordinate(5, 5)

  "TileCoordinate" should "find correct x axis right distance" in {
    coord.distanceTo(TileCoordinate(10, 5)) should be (5)
  }

  "TileCoordinate" should "find correct diagonal distance" in {
    coord.distanceTo(TileCoordinate(8, 9)) should be (5)
  }
}