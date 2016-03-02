package org.danielholmes.coc.baseanalyser.model

object TileCoordinate {
  val MaxSize: Int = 44
}

case class TileCoordinate(x: Int, y: Int) {
  require(x <= TileCoordinate.MaxSize && x >= 0, "TileCoordinates.x must be >= 0 <= " + TileCoordinate.MaxSize)
  require(y <= TileCoordinate.MaxSize && y >= 0, "TileCoordinates.y must be >= 0 <= " + TileCoordinate.MaxSize)
}
