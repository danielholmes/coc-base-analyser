package org.danielholmes.coc.baseanalyser.model

case class BarbarianKing(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) with Defense {
  val range = 0 to 8
  val targets = Set(Target.Ground)
  val size = TileSize(3)
}
