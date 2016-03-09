package org.danielholmes.coc.baseanalyser.model

case class Barrack(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
