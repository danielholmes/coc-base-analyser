package org.danielholmes.coc.baseanalyser.model

case class GiantBomb(level: Int, coordinate: TileCoordinate) extends Trap {
  val size = TileSize(2)
}
