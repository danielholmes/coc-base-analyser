package org.danielholmes.coc.baseanalyser.model

case class ArmyCamp(val level: Int, tile: Tile) extends Building {
  val size = TileSize(5)
  override lazy val hitSize = TileSize(3)
}
