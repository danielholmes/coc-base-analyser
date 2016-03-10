package org.danielholmes.coc.baseanalyser.model

case class SpellFactory(level: Int, tile: Tile) extends Building {
  val size = TileSize(3)
}
