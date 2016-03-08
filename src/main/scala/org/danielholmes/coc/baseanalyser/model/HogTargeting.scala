package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.{Segment, Line, Vector2D}

case class HogTargeting(startPosition: TileCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)

  def cutsRadius(radius: ElementRange): Boolean = {
    asSegment.distance(coordinateAsPoint(radius.coordinate)) < radius.outerSize.toInt
  }

  private val asLine = new Line(coordinateAsPoint(startPosition), coordinateAsPoint(hitPoint), 0.01)

  private val asSegment = new Segment(coordinateAsPoint(startPosition), coordinateAsPoint(hitPoint), asLine)

  private def coordinateAsPoint(coordinate: TileCoordinate): Vector2D = coordinateAsPoint(coordinate.toMapCoordinate)

  private def coordinateAsPoint(coordinate: MapCoordinate): Vector2D = new Vector2D(coordinate.x, coordinate.y)
}
