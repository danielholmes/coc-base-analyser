package org.danielholmes.coc.baseanalyser.model.range

import org.danielholmes.coc.baseanalyser.model.MapCoordinate
import org.scalactic.anyvals.PosDouble

case class BlindSpotCircularElementRange(centre: MapCoordinate, innerSize: PosDouble, outerSize: PosDouble) extends ElementRange {
  require(innerSize < outerSize, "inner should be less than outer")

  def contains(testCoordinate: MapCoordinate): Boolean = {
    val distance = testCoordinate.distanceTo(centre)
    distance >= innerSize && distance < outerSize
  }
}
