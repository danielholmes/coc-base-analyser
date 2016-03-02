package org.danielholmes.coc.baseanalyser.model

case class TownHall(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(4)
}
