package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._

object HogRider {
  def findTarget(coordinate: MapTileCoordinate, village: Village): Option[HogTargeting] = {
    if (village.isEmpty) return None
    Some(HogTargeting(coordinate, findTargetInNonEmptyVillage(coordinate, village)))
  }

  private def findTargetInNonEmptyVillage(coordinate: MapTileCoordinate, village: Village): Element = {
    val defenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .filterNot(_.isInstanceOf[Hero])
      .map(_.asInstanceOf[Defense])
    if (defenses.isEmpty) return village.elements.minBy(_.block.distanceFrom(coordinate))
    defenses.minBy(_.block.distanceFrom(coordinate))
  }
}
