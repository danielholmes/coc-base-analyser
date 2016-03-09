package org.danielholmes.coc.baseanalyser.model

case class InfernoTower(level: Int, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(8))
  val targets = Target.Both
  val size = TileSize(2)
}
