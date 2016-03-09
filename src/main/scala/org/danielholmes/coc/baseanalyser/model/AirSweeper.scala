package org.danielholmes.coc.baseanalyser.model

case class AirSweeper(level: Int, tile: Tile) extends Element {
  val size = TileSize(2)
}
