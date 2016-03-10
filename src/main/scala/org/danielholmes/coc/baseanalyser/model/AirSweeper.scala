package org.danielholmes.coc.baseanalyser.model

// TODO: Figure out what type of building it is exactly, not a defense like others
case class AirSweeper(level: Int, tile: Tile) extends Building {
  val size = TileSize(2)
}
