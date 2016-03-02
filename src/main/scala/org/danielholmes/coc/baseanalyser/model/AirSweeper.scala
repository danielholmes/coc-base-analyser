package org.danielholmes.coc.baseanalyser.model

case class AirSweeper(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(2)
}
