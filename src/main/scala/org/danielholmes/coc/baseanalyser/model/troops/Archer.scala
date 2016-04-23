package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosDouble

object Archer {
  val Range = PosDouble(3.5)

  // TODO: should be multiple targets in case equidistant
  def findTarget(coordinate: TileCoordinate, village: Village): Option[ArcherTargeting] = {
    if (village.buildings.isEmpty) return None
    Some(village.buildings.minBy(_.hitBlock.distanceTo(coordinate)))
      .filter(_.hitBlock.distanceTo(coordinate) < Range)
      .map(ArcherTargeting(coordinate, _))
  }
}
