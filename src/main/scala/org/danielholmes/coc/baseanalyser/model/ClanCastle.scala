package org.danielholmes.coc.baseanalyser.model

case class ClanCastle(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)

  def radius = Radius(block.centre, TileSize(12))
}
