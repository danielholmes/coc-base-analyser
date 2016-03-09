package org.danielholmes.coc.baseanalyser.model

case class AirBomb(level: Int, tile: Tile) extends Trap {
  val size = TileSize(1)
}
