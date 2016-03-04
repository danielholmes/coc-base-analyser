package org.danielholmes.coc.baseanalyser.model

case class Mortar(val level: Int, coordinate: TileCoordinate) extends Defense {
  // TODO: Blind spot
  def radius = Radius(block.centre, TileSize(11))
  val size = TileSize(3)
  val targets = Set(Target.Ground)
}
