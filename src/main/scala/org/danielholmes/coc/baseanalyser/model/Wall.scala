package org.danielholmes.coc.baseanalyser.model

case class Wall(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(1)
}