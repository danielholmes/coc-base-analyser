package org.danielholmes.coc.baseanalyser.model

case class ClanCastle(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)

  lazy val radius = Radius(block.centre, TileSize(12))
}
