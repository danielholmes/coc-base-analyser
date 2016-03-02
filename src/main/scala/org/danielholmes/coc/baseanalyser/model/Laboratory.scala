package org.danielholmes.coc.baseanalyser.model

case class Laboratory(val level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(5)
}
