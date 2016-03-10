package org.danielholmes.coc.baseanalyser.model

case class Laboratory(val level: Int, tile: Tile) extends Building {
  val size = TileSize(4)
}
