package org.danielholmes.coc.baseanalyser.model.range

import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Tile}
import org.scalactic.anyvals.{PosDouble, PosInt, PosZDouble}

// TODO: Make angle a reduced amount
case class WedgeElementRange(centre: MapCoordinate, angle: PosZDouble, size: PosInt) extends ElementRange {
  def contains(testCoordinate: MapCoordinate): Boolean = {
    throw new NotImplementedError()
  }

  def touchesEdge(tile: Tile): Boolean = {
    throw new NotImplementedError()
  }

  def cutBy(segment: Segment): Boolean = {
    throw new NotImplementedError()
  }
}
