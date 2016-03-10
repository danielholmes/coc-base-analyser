package org.danielholmes.coc.baseanalyser.model

case class ElixirStorage(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
