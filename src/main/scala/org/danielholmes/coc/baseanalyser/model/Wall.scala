package org.danielholmes.coc.baseanalyser.model

case class Wall(level: Int, tile: Tile) extends Element {
  val size = TileSize(1)
}
