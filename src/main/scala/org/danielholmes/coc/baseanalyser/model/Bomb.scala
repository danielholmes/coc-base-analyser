package org.danielholmes.coc.baseanalyser.model

case class Bomb(level: Int, coordinate: TileCoordinate) extends Trap {
  val size = TileSize(1)
}
