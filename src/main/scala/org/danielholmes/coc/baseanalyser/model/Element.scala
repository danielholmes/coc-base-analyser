package org.danielholmes.coc.baseanalyser.model

trait Element {
  val level: Int
  val coordinate: TileCoordinate
  val size: TileSize
  val attackPlacementSize: TileSize = size
  val hitSize: TileSize = size

  require(level >= 1, "level should be >= 1")
}