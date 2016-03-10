package org.danielholmes.coc.baseanalyser.model

case class DarkElixirCollector(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
