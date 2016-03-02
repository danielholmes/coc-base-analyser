package org.danielholmes.coc.baseanalyser.model

case class Barrack(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(3)
}
