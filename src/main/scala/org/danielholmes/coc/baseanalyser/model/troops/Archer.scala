package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._

object Archer {
  val Range = 3.5

  def findTarget(coordinate: TileCoordinate, village: Village): Option[ArcherTargeting] = {
    if (village.isEmpty) return None
    if (village.buildings.isEmpty) return None
    Some(village.buildings.minBy(_.hitBlock.distanceFrom(coordinate)))
      .filter(_.hitBlock.distanceFrom(coordinate) <= Range)
      .map(ArcherTargeting(coordinate, _))
  }
}
