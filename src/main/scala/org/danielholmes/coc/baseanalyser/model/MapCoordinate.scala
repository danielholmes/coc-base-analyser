package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosZDouble
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

case class MapCoordinate(x: PosZDouble, y: PosZDouble) {
  require(x >= 0.0 && x <= TileCoordinate.MaxCoordinate, s"MapCoordinates.x must be >= 0 <= ${TileCoordinate.MaxCoordinate}, given: $x")
  require(y >= 0.0 && y <= TileCoordinate.MaxCoordinate, s"MapCoordinates.y must be >= 0 <= ${TileCoordinate.MaxCoordinate}, given: $y")

  def distanceTo(other: MapCoordinate): PosZDouble = {
    PosZDouble.from(Math.hypot(x - other.x, y - other.y)).get
  }
}

object MapCoordinate {
  implicit def widenToVector2D(coord: MapCoordinate): Vector2D = new Vector2D(coord.x, coord.y)
}
