package org.danielholmes.coc.baseanalyser.model

case class GoldMine(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(3)
}
