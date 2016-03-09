package org.danielholmes.coc.baseanalyser.model

case class Cannon(level: Int, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(10))
  val size = TileSize(3)
  val targets = Set(Target.Ground)
}
