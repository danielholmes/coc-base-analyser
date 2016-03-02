package org.danielholmes.coc.baseanalyser.model

case class AirSweeper(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(2)
}
