package org.danielholmes.coc.baseanalyser.model

case class ArmyCamp(val level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(5)
  override val hitSize = TileSize(3)
}
