package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.Segment

trait ElementRange {
  def contains(testCoordinate: MapCoordinate): Boolean

  def cutBy(segment: Segment): Boolean

  def touchesEdge(tile: Tile): Boolean

  def touches(tile: Tile): Boolean = tile.allCoordinates.exists(contains(_))

  def touches(block: Block): Boolean = block.allCoordinates.exists(contains(_))

  lazy val allTouchingTiles = Tile.All.filter(touches)
}
