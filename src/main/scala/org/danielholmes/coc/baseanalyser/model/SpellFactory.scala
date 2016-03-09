package org.danielholmes.coc.baseanalyser.model

case class SpellFactory(level: Int, tile: Tile) extends Element {
  val size = TileSize(3)
}
