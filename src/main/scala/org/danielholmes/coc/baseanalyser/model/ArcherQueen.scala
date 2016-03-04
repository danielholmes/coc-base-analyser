package org.danielholmes.coc.baseanalyser.model

case class ArcherQueen(level: Int, coordinate: TileCoordinate) extends Defense {
  lazy val radius = Radius(block.centre, TileSize(8))
  val targets = Target.Both
  val size = TileSize(3)
}
