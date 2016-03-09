package org.danielholmes.coc.baseanalyser.model

case class AirDefense(level: Int, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(10))
  val targets = Set(Target.Air)
  val size = TileSize(3)
}
