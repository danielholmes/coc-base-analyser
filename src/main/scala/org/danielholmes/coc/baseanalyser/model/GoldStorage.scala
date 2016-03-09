package org.danielholmes.coc.baseanalyser.model

case class GoldStorage(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
