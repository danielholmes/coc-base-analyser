package org.danielholmes.coc.baseanalyser.model.range

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Tile}
import org.scalactic.anyvals.PosDouble

case class BlindSpotCircularElementRange(centre: MapCoordinate, innerSize: PosDouble, outerSize: PosDouble) extends ElementRange {
  require(innerSize < outerSize, "inner should be less than outer")

  def contains(testCoordinate: MapCoordinate): Boolean = {
    val distance = testCoordinate.distanceTo(centre)
    distance >= innerSize && distance < outerSize
  }

  def touchesEdge(tile: Tile): Boolean = {
    val touchResults = tile.allCoordinates.partition(contains(_))
    touchResults._1.nonEmpty && touchResults._2.nonEmpty
  }

  // TODO: This doesnt take into account segments that are entirely within inner size. TODO: unit test this
  def cutBy(segment: Segment): Boolean = {
    segment.distance(centre) < outerSize
  }
}
