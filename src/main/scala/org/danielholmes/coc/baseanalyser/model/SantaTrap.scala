package org.danielholmes.coc.baseanalyser.model

case class SantaTrap(level: Int, coordinate: TileCoordinate) extends Trap {
  val size = TileSize(1)
}
