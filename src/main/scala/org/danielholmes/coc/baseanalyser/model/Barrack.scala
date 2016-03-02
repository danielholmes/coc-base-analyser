package org.danielholmes.coc.baseanalyser.model

case class Barrack(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)
}
