package org.danielholmes.coc.baseanalyser.model

case class ClanCastle(level: Int, coordinate: TileCoordinate) extends Element {
  val size = TileSize(3)

  lazy val range = ElementRange(block.centre, TileSize(12))
}
