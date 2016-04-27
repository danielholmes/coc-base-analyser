package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosDouble

object Archer {
  val Range = PosDouble(3.5)

  def findTargets(coordinate: TileCoordinate, village: Village): Set[ArcherTargeting] = {
    val buildingsByDistanceAway = village.buildings
      .groupBy(_.hitBlock.distanceTo(coordinate))
    buildingsByDistanceAway.keys
      .filter(_ < Range)
      .reduceOption(_ min _)
      .flatMap(d => buildingsByDistanceAway.get(d))
      .getOrElse(Set.empty)
      .map(ArcherTargeting(coordinate, _))
  }
}
