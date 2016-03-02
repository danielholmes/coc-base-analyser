package org.danielholmes.coc.baseanalyser.model

case class WizardTower(level: Int, coordinate: TileCoordinate) extends Element(level, coordinate) with Defense {
  val range = 0 to 8
  val targets = Target.Both
  val size = TileSize(3)
}
