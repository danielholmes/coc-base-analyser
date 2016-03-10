package org.danielholmes.coc.baseanalyser.model

case class ElementRange(coordinate: MapCoordinate, outerSize: TileSize, innerSize: TileSize = TileSize(0)) {
  def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < outerSize.toInt
  }
}