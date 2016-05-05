package org.danielholmes.coc.baseanalyser.model.range

import org.danielholmes.coc.baseanalyser.model.{FloatMapCoordinate, Tile, TileCoordinate}
import org.scalactic.anyvals.{PosDouble, PosZInt}

case class BlindSpotCircularElementRange(centre: FloatMapCoordinate, innerSize: PosDouble, outerSize: PosDouble) extends ElementRange {
  require(innerSize < outerSize, "inner should be less than outer")

  def contains(testCoordinate: FloatMapCoordinate): Boolean = {
    val distance = testCoordinate.distanceTo(centre)
    distance >= innerSize && distance < outerSize
  }
}
