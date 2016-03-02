package org.danielholmes.coc.baseanalyser.model

case class TownHall(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(4)
}
