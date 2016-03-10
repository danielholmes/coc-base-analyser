package org.danielholmes.coc.baseanalyser.model

case class BuilderHut(level: Int, tile: Tile) extends Building {
  val size = TileSize(2)
}
