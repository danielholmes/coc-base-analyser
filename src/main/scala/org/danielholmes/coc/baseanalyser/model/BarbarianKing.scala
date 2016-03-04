package org.danielholmes.coc.baseanalyser.model

case class BarbarianKing(level: Int, coordinate: TileCoordinate) extends Defense {
  def radius = Radius(block.centre, TileSize(8))
  val targets = Set(Target.Ground)
  val size = TileSize(3)
}
