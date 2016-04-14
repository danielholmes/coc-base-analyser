package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.{PosInt, PosZInt}

case class ElementRange(coordinate: MapCoordinate, outerSize: PosInt, innerSize: PosZInt = 0) {
  def contains(testCoordinate: TileCoordinate): Boolean = {
    testCoordinate.distanceTo(coordinate) < outerSize
  }

  def touches(tile: Tile) = tile.allCoordinates.exists(contains)

  def touches(block: Block) = block.allCoordinates.exists(contains)

  lazy val allTouchingTiles = Tile.All.filter(touches)
}