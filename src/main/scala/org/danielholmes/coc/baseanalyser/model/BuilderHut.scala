package org.danielholmes.coc.baseanalyser.model

case class BuilderHut(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(2)
}
