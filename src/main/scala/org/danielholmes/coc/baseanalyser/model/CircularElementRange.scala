package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.scalactic.anyvals.PosInt

case class CircularElementRange(centre: MapCoordinate, size: PosInt) extends ElementRange {
  def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(centre) < size
  }

  def touchesEdge(tile: Tile) = {
    Math.abs(tile.toMapCoordinate.distanceTo(centre) - size) > 0.5
  }

  def cutBy(segment: Segment): Boolean = {
    segment.distance(centre.toVector2D) < size
  }
}