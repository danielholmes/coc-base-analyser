package org.danielholmes.coc.baseanalyser.model

case class Bomb(level: Int, tile: Tile) extends Trap {
  val size = TileSize(1)
}
