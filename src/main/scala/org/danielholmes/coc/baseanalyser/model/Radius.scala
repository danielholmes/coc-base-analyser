package org.danielholmes.coc.baseanalyser.model

case class Radius(coordinate: MapCoordinate, size: TileSize) {
  def containsAll(coordinates: Set[TileCoordinate]): Boolean = {
    if (coordinates.isEmpty) return true
    contains(coordinates.head) && containsAll(coordinates.tail)
  }

  private def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < size.toInt
  }
}