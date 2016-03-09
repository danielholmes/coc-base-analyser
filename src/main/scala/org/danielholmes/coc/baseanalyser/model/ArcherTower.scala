package org.danielholmes.coc.baseanalyser.model

case class ArcherTower(level: Int, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(11))
  val targets = Target.Both
  val size = TileSize(3)
}
