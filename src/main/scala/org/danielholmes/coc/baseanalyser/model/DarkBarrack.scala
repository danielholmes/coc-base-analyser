package org.danielholmes.coc.baseanalyser.model

case class DarkBarrack(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
