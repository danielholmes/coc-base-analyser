package org.danielholmes.coc.baseanalyser.model

case class ArcherTower(level: Int, coordinate: TileCoordinate) extends Defense {
  lazy val radius = Radius(block.centre, TileSize(11))
  val targets = Target.Both
  val size = TileSize(3)
}
