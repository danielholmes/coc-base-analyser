package org.danielholmes.coc.baseanalyser.model

case class TeslaTower(level: Int, coordinate: TileCoordinate) extends Defense {
  def radius = Radius(block.centre, TileSize(6))
  val targets = Target.Both
  val size = TileSize(2)
  override val attackPlacementSize: TileSize = TileSize(0)
}
