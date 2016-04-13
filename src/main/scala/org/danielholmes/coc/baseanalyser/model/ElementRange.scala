package org.danielholmes.coc.baseanalyser.model

case class ElementRange(coordinate: MapCoordinate, outerSize: TileSize, innerSize: TileSize = TileSize(0)) {
  def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < outerSize.toInt
  }

  def touches(tile: Tile) = tile.allCoordinates.exists(contains)

  def touches(block: Block) = block.allCoordinates.exists(contains)

  lazy val allTouchingTiles = Tile.All.filter(touches)
}