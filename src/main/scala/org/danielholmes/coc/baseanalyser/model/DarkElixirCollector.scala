package org.danielholmes.coc.baseanalyser.model

case class DarkElixirCollector(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
