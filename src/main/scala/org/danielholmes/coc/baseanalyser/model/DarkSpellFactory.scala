package org.danielholmes.coc.baseanalyser.model

case class DarkSpellFactory(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
