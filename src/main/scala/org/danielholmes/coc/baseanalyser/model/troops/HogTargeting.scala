package org.danielholmes.coc.baseanalyser.model.troops

import org.apache.commons.math3.geometry.euclidean.twod.{Line, Segment, Vector2D}
import org.danielholmes.coc.baseanalyser.model.{Element, ElementRange, MapCoordinate, TileCoordinate}

case class HogTargeting(startPosition: TileCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)

  def cutsRadius(range: ElementRange): Boolean = {
    range.cutBy(asSegment)
  }

  private val asLine = new Line(coordinateAsPoint(startPosition), coordinateAsPoint(hitPoint), 0.01)

  private val asSegment = new Segment(coordinateAsPoint(startPosition), coordinateAsPoint(hitPoint), asLine)

  private def coordinateAsPoint(coordinate: TileCoordinate): Vector2D = coordinate.toMapCoordinate.toVector2D
}
