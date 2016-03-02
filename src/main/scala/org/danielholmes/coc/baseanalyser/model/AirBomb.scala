package org.danielholmes.coc.baseanalyser.model

case class AirBomb(level: Int, coordinate: TileCoordinate) extends Trap {
  val size = TileSize(1)
}
