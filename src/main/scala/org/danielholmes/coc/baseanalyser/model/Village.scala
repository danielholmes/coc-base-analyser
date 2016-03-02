package org.danielholmes.coc.baseanalyser.model

case class Village(val elements: Set[Element]) {
  def townHallLevel(): Option[Int] = {
    elements.find(_.isInstanceOf[TownHall])
      .map(_.asInstanceOf[TownHall])
      .map(_.level)
  }

  val attackPlacementCoordinates = TileCoordinate.All
}

object Village {
  val empty = Village(Set.empty)
}