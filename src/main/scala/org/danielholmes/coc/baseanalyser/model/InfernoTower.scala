package org.danielholmes.coc.baseanalyser.model

case class InfernoTower(level: Int, coordinate: TileCoordinate) extends Defense {
  val range = 0 to 8
  val targets = Target.Both
  val size = TileSize(2)
}
