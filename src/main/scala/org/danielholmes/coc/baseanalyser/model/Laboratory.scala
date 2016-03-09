package org.danielholmes.coc.baseanalyser.model

case class Laboratory(val level: Int, tile: Tile) extends Element {
  val size = TileSize(4)
}
