package org.danielholmes.coc.baseanalyser.model

case class ArcherTower(level: Int, coordinate: TileCoordinate) extends Defense {
  val range = 0 to 11
  val targets = Target.Both
  val size = TileSize(3)
}
