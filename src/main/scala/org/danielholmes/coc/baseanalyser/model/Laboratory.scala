package org.danielholmes.coc.baseanalyser.model

case class Laboratory(val level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(5)
}
