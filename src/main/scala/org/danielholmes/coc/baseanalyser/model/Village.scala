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

  lazy val isEmpty = elements.isEmpty

  lazy val buildings = elements.filter(_.isInstanceOf[Building]).map(_.asInstanceOf[Building])

  /* private */ lazy val tilesNotAllowedToDropTroop = elements.flatMap(_.preventTroopDropBlock.tiles)

  /* private */ lazy val tilesAllowedToDropTroop = Tile.All -- tilesNotAllowedToDropTroop

  lazy val coordinatesAllowedToDropTroop: Set[TileCoordinate] = tilesAllowedToDropTroop.flatMap(_.allCoordinates) ++ TileCoordinate.AllEdge
}

object Village {
val empty = Village(Set.empty)
}