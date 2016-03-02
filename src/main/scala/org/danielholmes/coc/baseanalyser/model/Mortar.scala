package org.danielholmes.coc.baseanalyser.model

case class Mortar(val level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) with Defense {
  val range = 4 to 11
  val size = TileSize(3)
  val targets = Set(Target.Ground)
}
