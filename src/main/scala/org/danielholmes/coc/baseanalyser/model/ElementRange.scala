package org.danielholmes.coc.baseanalyser.model

case class ElementRange(coordinate: MapCoordinate, outerSize: TileSize, innerSize: TileSize = TileSize(0)) {
  def containsAll(coordinates: Set[MapTileCoordinate]): Boolean = {
    if (coordinates.isEmpty) return true
    contains(coordinates.head) && containsAll(coordinates.tail)
  }

  private def contains(testCoordinate: MapTileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < outerSize.toInt
  }
}