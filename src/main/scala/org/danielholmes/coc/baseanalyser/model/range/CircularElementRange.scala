package org.danielholmes.coc.baseanalyser.model.range

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.danielholmes.coc.baseanalyser.model.{FloatMapCoordinate, Tile}
import org.scalactic.anyvals.PosDouble

case class CircularElementRange(centre: FloatMapCoordinate, size: PosDouble) extends ElementRange {
  def contains(testCoordinate: FloatMapCoordinate): Boolean = {
    testCoordinate.distanceTo(centre) < size
  }

  def inset(amount: PosDouble): CircularElementRange = {
    CircularElementRange(centre, PosDouble.from(size - amount).get)
  }

  def cutBy(segment: Segment): Boolean = segment.distance(centre) < size
}
