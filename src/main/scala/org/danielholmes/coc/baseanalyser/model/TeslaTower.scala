package org.danielholmes.coc.baseanalyser.model

case class TeslaTower(level: Int, coordinate: TileCoordinate) extends Defense {
  lazy val radius = Radius(block.centre, TileSize(6))
  val targets = Target.Both
  val size = TileSize(2)
  override lazy val attackPlacementSize: TileSize = TileSize(0)
}
