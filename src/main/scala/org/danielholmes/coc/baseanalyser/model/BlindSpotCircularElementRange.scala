package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.scalactic.anyvals.PosInt

case class BlindSpotCircularElementRange(centre: MapCoordinate, innerSize: PosInt, outerSize: PosInt) extends ElementRange {
  require(innerSize < outerSize, "inner should be less than outer")

  def contains(testCoordinate: TileCoordinate): Boolean = {
    val distance = testCoordinate.distanceTo(centre)
    distance > innerSize && distance < outerSize
  }

  def touchesEdge(tile: Tile) = {
    Math.abs(tile.toMapCoordinate.distanceTo(centre) - outerSize) > 0.5
  }

  // TODO: This doesnt take into account segments that are entirely within inner size. TODO: unit test this
  def cutBy(segment: Segment): Boolean = {
    segment.distance(centre.toVector2D) < outerSize
  }
}