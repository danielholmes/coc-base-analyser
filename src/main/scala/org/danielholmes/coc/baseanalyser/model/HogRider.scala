package org.danielholmes.coc.baseanalyser.model

object HogRider {
  def findTarget(coordinate: TileCoordinate, village: Village): Option[HogTargeting] = {
    if (village.isEmpty) return None
    Some(HogTargeting(coordinate, findTarget(coordinate, village.elements)))
  }

  private def findTarget(coordinate: TileCoordinate, elements: Set[Element]): Element = {
    val defenses = elements.filter(_.isInstanceOf[Defense])
    if (defenses.isEmpty) return elements.minBy(_.block.distanceFrom(coordinate))
    defenses.minBy(_.block.distanceFrom(coordinate))
  }
}
