package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._

object HogRider {
  def findTargets(coordinate: TileCoordinate, village: Village): Set[HogTargeting] = {
    if (village.isEmpty) return Set.empty
    findTargetsInNonEmptyVillage(coordinate, village)
      .map(HogTargeting(coordinate, _))
  }

  private def findTargetsInNonEmptyVillage(coordinate: TileCoordinate, village: Village): Set[Element] = {
    val defenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .filterNot(_.isInstanceOf[Hero])
      .map(_.asInstanceOf[Defense])
    if (defenses.nonEmpty) return getAllClosest(coordinate, defenses.map(_.asInstanceOf[Element]))
    if (village.buildings.isEmpty) return Set.empty

    // TODO: Unit test using hit block and not visual
    getAllClosest(coordinate, village.buildings.map(_.asInstanceOf[Element]))
  }

  private def getAllClosest(coordinate: TileCoordinate, elements: Set[Element]): Set[Element] = {
    val groupedByDistance = elements.groupBy(_.hitBlock.distanceFrom(coordinate))
    groupedByDistance.get(groupedByDistance.keySet.min).get
  }
}
