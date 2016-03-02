package org.danielholmes.coc.baseanalyser.model

case class ClanCastle(level: Int, coordinate: TileCoordinate) extends Element {
  val range = 0 to 12
  val size = TileSize(3)
}
