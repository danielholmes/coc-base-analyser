package org.danielholmes.coc.baseanalyser.model

object TileCoordinate {
  val Max: Int = 44
}

case class TileCoordinate(x: Int, y: Int) {
  require(x <= TileCoordinate.Max && x >= 0, "TileCoordinates.x must be >= 0 <= " + TileCoordinate.Max)
  require(y <= TileCoordinate.Max && y >= 0, "TileCoordinates.y must be >= 0 <= " + TileCoordinate.Max)
}
