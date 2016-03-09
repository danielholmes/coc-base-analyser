package org.danielholmes.coc.baseanalyser.model

case class ArcherQueen(level: Int, tile: Tile) extends Hero {
  lazy val range = ElementRange(block.centre, TileSize(8))
  val targets = Target.Both
  val size = TileSize(3)
}
