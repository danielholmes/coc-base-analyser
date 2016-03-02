package org.danielholmes.coc.baseanalyser.model

abstract class Element(level: Int, coordinate: TileCoordinate) {
  require(level >= 1, "level should be >= 1")

  val size: TileSize

  val attackPlacementSize: TileSize = size

  val hitSize: TileSize = size
}
