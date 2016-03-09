package org.danielholmes.coc.baseanalyser.model

case class GiantBomb(level: Int, tile: Tile) extends Trap {
  val size = TileSize(2)
}
