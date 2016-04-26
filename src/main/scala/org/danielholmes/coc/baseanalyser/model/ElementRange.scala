package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.Segment

trait ElementRange {
  def contains(testCoordinate: TileCoordinate): Boolean

  def contains(testCoordinate: MapCoordinate): Boolean

  def cutBy(segment: Segment): Boolean

  def touchesEdge(tile: Tile): Boolean

  def touches(tile: Tile) = tile.allCoordinates.exists(contains)

  def touches(block: Block) = block.allCoordinates.exists(contains)

  lazy val allTouchingTiles = Tile.All.filter(touches)
}