package org.danielholmes.coc.baseanalyser.model

case class TownHall(level: Int, tile: Tile) extends Building {
  val size = TileSize(4)
}
