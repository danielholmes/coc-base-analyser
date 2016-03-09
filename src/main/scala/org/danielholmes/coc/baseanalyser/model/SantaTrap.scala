package org.danielholmes.coc.baseanalyser.model

case class SantaTrap(level: Int, tile: Tile) extends Trap {
  val size = TileSize(1)
}
