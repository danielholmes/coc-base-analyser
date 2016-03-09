package org.danielholmes.coc.baseanalyser.model

case class BuilderHut(level: Int, tile: Tile) extends Element {
  val size = TileSize(2)
}
