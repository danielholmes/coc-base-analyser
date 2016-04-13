package org.danielholmes.coc.baseanalyser.model

case class MapCoordinate(x: Double, y: Double) {
  require(x >= 0.0 && x <= TileCoordinate.Max.toInt, s"MapCoordinates.x must be >= 0 <= ${TileCoordinate.Max}, given: $x")
  require(y >= 0.0 && y <= TileCoordinate.Max.toInt, s"MapCoordinates.y must be >= 0 <= ${TileCoordinate.Max}, given: $y")

  def distanceTo(other: MapCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }
}
