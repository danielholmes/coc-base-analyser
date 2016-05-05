package org.danielholmes.coc.baseanalyser.model.range

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Tile}
import org.scalactic.anyvals.PosDouble

case class CircularElementRange(centre: MapCoordinate, size: PosDouble) extends ElementRange {
  def contains(testCoordinate: MapCoordinate): Boolean = {
    testCoordinate.distanceTo(centre) < size
  }

  def inset(amount: PosDouble): CircularElementRange = {
    CircularElementRange(centre, PosDouble.from(size - amount).get)
  }

  def cutBy(segment: Segment): Boolean = {
    segment.distance(centre) < size
  }
}
