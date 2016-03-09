package org.danielholmes.coc.baseanalyser.model

case class BarbarianKing(level: Int, tile: Tile) extends Hero {
  lazy val range = ElementRange(block.centre, TileSize(8))
  val targets = Set(Target.Ground)
  val size = TileSize(3)
}
