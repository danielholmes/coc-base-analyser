package org.danielholmes.coc.baseanalyser.model

case class GoldStorage(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)
}
