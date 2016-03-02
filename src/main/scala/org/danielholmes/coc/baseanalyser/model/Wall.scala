package org.danielholmes.coc.baseanalyser.model

case class Wall(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(1)
}
