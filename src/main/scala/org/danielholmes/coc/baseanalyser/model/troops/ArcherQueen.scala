package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosZDouble

object ArcherQueen {
  val Range = PosZDouble(5)

  def firstPossibleAttackingCoordinate(element: Element, coordinates: Set[TileCoordinate]): Option[TileCoordinate] = {
    coordinates.find(element.block.distanceTo(_) < Range)
  }
}
