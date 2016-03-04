package org.danielholmes.coc.baseanalyser.model

case class AirDefense(level: Int, coordinate: TileCoordinate) extends Defense {
  def radius = Radius(block.centre, TileSize(10))
  val targets = Set(Target.Air)
  val size = TileSize(3)
}
