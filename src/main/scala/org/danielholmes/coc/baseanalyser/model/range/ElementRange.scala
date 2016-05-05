package org.danielholmes.coc.baseanalyser.model.range

import org.danielholmes.coc.baseanalyser.model.{Block, MapCoordinate, Tile}

trait ElementRange {
  def contains(testCoordinate: MapCoordinate): Boolean

  def touchesEdge(tile: Tile): Boolean = {
    val touchResults = tile.allCoordinates.partition(contains(_))
    touchResults._1.nonEmpty && touchResults._2.nonEmpty
  }

  def touches(tile: Tile): Boolean = tile.allCoordinates.exists(contains(_))

  def touches(block: Block): Boolean = block.allCoordinates.exists(contains(_))

  lazy val allTouchingTiles = Tile.All.filter(touches)
}
