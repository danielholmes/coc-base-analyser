package org.danielholmes.coc.baseanalyser.model.troops

import org.apache.commons.math3.geometry.euclidean.twod.{Line, Segment}
import org.danielholmes.coc.baseanalyser.model.range.{CircularElementRange, ElementRange}
import org.danielholmes.coc.baseanalyser.model.{Element, PreventsTroopDrop, Structure, TileCoordinate}

case class HogTargeting(startPosition: TileCoordinate, targeting: Structure) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)

  def cutsRadius(range: CircularElementRange): Boolean = range.cutBy(asSegment)

  private val asLine = new Line(startPosition, hitPoint, 0.01)

  private val asSegment = new Segment(startPosition, hitPoint, asLine)
}
