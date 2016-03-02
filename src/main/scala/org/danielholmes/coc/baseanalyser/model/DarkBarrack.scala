package org.danielholmes.coc.baseanalyser.model

case class DarkBarrack(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(3)
}
