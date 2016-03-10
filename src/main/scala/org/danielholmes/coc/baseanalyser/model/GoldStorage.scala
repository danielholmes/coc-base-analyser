package org.danielholmes.coc.baseanalyser.model

case class GoldStorage(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
