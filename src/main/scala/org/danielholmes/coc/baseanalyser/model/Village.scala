package org.danielholmes.coc.baseanalyser.model

case class Village(val elements: Set[Element]) {
  require(!Block.anyIntersect(elements.map(_.block)), "Elements musn't overlap")

  val townHallLevel = elements.find(_.isInstanceOf[TownHall])
    .map(_.asInstanceOf[TownHall])
    .map(_.level)

  val clanCastle = elements.find(_.isInstanceOf[ClanCastle])
    .map(_.asInstanceOf[ClanCastle])

  def isEmpty = elements.isEmpty

  // Doesnt seem to work
  /*def findElementsByType[T <: Element]: Set[T] = {
    elements.filter(_.isInstanceOf[T])
      .map(_.asInstanceOf[T])
  }*/

  private val elementAttackPlacements = elements.map(_.attackPlacementBlock)
    .flatMap(_.internalCoordinates)

  val attackPlacementCoordinates: Set[TileCoordinate] = TileCoordinate.AllElementPlacement.toSet -- elementAttackPlacements
}

object Village {
  val empty = Village(Set.empty)
}