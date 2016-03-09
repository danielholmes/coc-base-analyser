package org.danielholmes.coc.baseanalyser.model

case class TownHall(level: Int, tile: Tile) extends Element {
  val size = TileSize(4)
}
