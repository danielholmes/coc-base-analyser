package org.danielholmes.coc.baseanalyser.model

case class AirDefense(level: Int, coordinate: TileCoordinate) extends Defense {
  val range = 0 to 10
  val targets = Set(Target.Air)
  val size = TileSize(3)
}
