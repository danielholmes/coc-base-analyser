package org.danielholmes.coc.baseanalyser.model

case class DarkElixirStorage(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
