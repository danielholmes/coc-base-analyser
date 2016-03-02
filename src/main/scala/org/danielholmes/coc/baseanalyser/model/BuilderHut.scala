package org.danielholmes.coc.baseanalyser.model

case class BuilderHut(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) {
  val size = TileSize(2)
}
