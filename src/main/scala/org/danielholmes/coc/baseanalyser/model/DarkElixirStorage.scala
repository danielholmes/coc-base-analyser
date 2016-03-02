package org.danielholmes.coc.baseanalyser.model

case class DarkElixirStorage(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)
}
