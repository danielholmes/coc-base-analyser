package org.danielholmes.coc.baseanalyser.model

case class MapCoordinate(x: Double, y: Double) {
  require(x >= 0.0 && x <= MapTileCoordinate.Max, s"MapCoordinates.x must be >= 0 <= ${MapTileCoordinate.Max}, given: $x")
  require(y >= 0.0 && y <= MapTileCoordinate.Max, s"MapCoordinates.y must be >= 0 <= ${MapTileCoordinate.Max}, given: $y")
}
