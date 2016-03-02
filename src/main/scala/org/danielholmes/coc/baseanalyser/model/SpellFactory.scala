package org.danielholmes.coc.baseanalyser.model

case class SpellFactory(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)
}
