package org.danielholmes.coc.baseanalyser.model

case class ElementRange(coordinate: MapCoordinate, outerSize: TileSize, innerSize: TileSize = TileSize(0)) {
  def containsAll(coordinates: Set[TileCoordinate]): Boolean = {
    if (coordinates.isEmpty) return true
    contains(coordinates.head) && containsAll(coordinates.tail)
  }

  private def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < outerSize.toInt
  }
}