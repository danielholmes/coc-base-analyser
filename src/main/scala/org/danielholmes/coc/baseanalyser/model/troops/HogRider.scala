package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._

object HogRider {
  def findTarget(coordinate: TileCoordinate, village: Village): Option[HogTargeting] = {
    if (village.isEmpty) return None
    findTargetInNonEmptyVillage(coordinate, village)
      .map(HogTargeting(coordinate, _))
  }

  private def findTargetInNonEmptyVillage(coordinate: TileCoordinate, village: Village): Option[Element] = {
    val defenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .filterNot(_.isInstanceOf[Hero])
      .map(_.asInstanceOf[Defense])
    if (defenses.nonEmpty) return Some(defenses.minBy(_.hitBlock.distanceFrom(coordinate)))
    if (village.buildings.isEmpty) return None
    Some(village.buildings.minBy(_.hitBlock.distanceFrom(coordinate))) // TODO: Unit test using hit block and not visual
  }
}
