package org.danielholmes.coc.baseanalyser.model

case class TeslaTower(level: Int, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(6))
  val targets = Target.Both
  val size = TileSize(2)
  override lazy val attackPlacementSize: TileSize = TileSize(0)
}
