package org.danielholmes.coc.baseanalyser.model

case class Mortar(val level: Int, coordinate: TileCoordinate) extends Defense {
  lazy val range = ElementRange(block.centre, TileSize(11), TileSize(4))
  val size = TileSize(3)
  val targets = Set(Target.Ground)
}
