package org.danielholmes.coc.baseanalyser.model

case class DarkElixirStorage(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
