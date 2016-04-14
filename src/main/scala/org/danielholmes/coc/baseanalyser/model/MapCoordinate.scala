package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosZDouble

case class MapCoordinate(x: PosZDouble, y: PosZDouble) {
  require(x >= 0.0 && x <= TileCoordinate.MaxCoordinate, s"MapCoordinates.x must be >= 0 <= ${TileCoordinate.MaxCoordinate}, given: $x")
  require(y >= 0.0 && y <= TileCoordinate.MaxCoordinate, s"MapCoordinates.y must be >= 0 <= ${TileCoordinate.MaxCoordinate}, given: $y")

  def distanceTo(other: MapCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }
}
