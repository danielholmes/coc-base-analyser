package org.danielholmes.coc.baseanalyser.model

object HogRider {
  def findTarget(coordinate: TileCoordinate, village: Village): Option[HogTargeting] = {
    if (village.isEmpty) return None
    Some(HogTargeting(coordinate, findTargetInNonEmptyVillage(coordinate, village)))
  }

  private def findTargetInNonEmptyVillage(coordinate: TileCoordinate, village: Village): Element = {
    val defenses = village.elements.filter(_.isInstanceOf[Defense]).map(_.asInstanceOf[Defense])
    if (defenses.isEmpty) return village.elements.minBy(_.block.distanceFrom(coordinate))
    defenses.minBy(_.block.distanceFrom(coordinate))
  }
}
