package org.danielholmes.coc.baseanalyser.model

case class Village(elements: Set[Element]) {
  require(
    !Block.anyIntersect(elements.map(_.block)),
    s"Elements musn't overlap (currently ${Block.getAnyIntersection(elements.map(_.block)).get} overlaps"
  )

  val townHallLevel = elements.find(_.isInstanceOf[TownHall])
    .map(_.asInstanceOf[TownHall])
    .map(_.level)

  val clanCastle = elements.find(_.isInstanceOf[ClanCastle])
    .map(_.asInstanceOf[ClanCastle])

  def isEmpty = elements.isEmpty

  /* private */ lazy val tilesNotAllowedToDropTroop = elements.flatMap(_.preventTroopDropBlock.tiles)

  /* private */ lazy val tilesAllowedToDropTroop = Tile.All -- tilesNotAllowedToDropTroop

  lazy val coordinatesAllowedToDropTroop: Set[MapTileCoordinate] = tilesAllowedToDropTroop.flatMap(_.allCoordinates)
}

object Village {
val empty = Village(Set.empty)
}