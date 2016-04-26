package org.danielholmes.coc.baseanalyser.model.troops

import org.apache.commons.math3.geometry.euclidean.twod.{Line, Segment}
import org.danielholmes.coc.baseanalyser.model.{Element, ElementRange, TileCoordinate}

case class HogTargeting(startPosition: TileCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)

  def cutsRadius(range: ElementRange): Boolean = {
    range.cutBy(asSegment)
  }

  private val asLine = new Line(startPosition.toVector2D, hitPoint.toVector2D, 0.01)

  private val asSegment = new Segment(startPosition.toVector2D, hitPoint.toVector2D, asLine)
}
