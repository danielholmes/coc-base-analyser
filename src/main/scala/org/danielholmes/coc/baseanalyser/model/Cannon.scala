package org.danielholmes.coc.baseanalyser.model

case class Cannon(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) with Defense {
  val range = 0 to 10
  val size = TileSize(3)
  val targets = Set(Target.Ground)
}
