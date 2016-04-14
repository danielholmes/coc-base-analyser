package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class Village(elements: Set[Element]) {
  require(
    !Block.anyIntersect(elements.map(_.block)),
    s"Elements musn't overlap (currently ${Block.getAnyIntersection(elements.map(_.block)).get} overlaps"
  )

  val townHallLevel: Option[PosInt] = elements.find(_.isInstanceOf[TownHall])
    .map(_.level)

  val clanCastle = elements.find(_.isInstanceOf[ClanCastle])
    .map(_.asInstanceOf[ClanCastle])

  lazy val isEmpty = elements.isEmpty

  lazy val buildings = elements.filter(_.isInstanceOf[Building]).map(_.asInstanceOf[Building])

  private lazy val tilesNotAllowedToDropTroop = elements.flatMap(_.preventTroopDropBlock.tiles)

  lazy val tilesAllowedToDropTroop = Tile.All -- tilesNotAllowedToDropTroop

  lazy val coordinatesAllowedToDropTroop: Set[TileCoordinate] = tilesAllowedToDropTroop.flatMap(_.allCoordinates) ++ TileCoordinate.AllEdge

  lazy val wallCompartments: Set[WallCompartment] = {
    val innerTiles = Tile.All -- outerTiles -- walls.map(_.block.tile)
    if (walls.isEmpty || innerTiles.isEmpty) {
      Set.empty
    } else {
      detectAllCompartments(innerTiles, Set.empty)
    }
  }

  lazy val wallTiles = walls.map(_.block.tile)

  private lazy val walls: Set[Wall] = elements.filter(_.isInstanceOf[Wall]).map(_.asInstanceOf[Wall])

  private lazy val outerTiles: Set[Tile] = detectCompartment(Tile.AllOutsideMap).innerTiles

  private def detectAllCompartments(innerTiles: Set[Tile], current: Set[WallCompartment]): Set[WallCompartment] = {
    if (innerTiles.isEmpty) return current
    val compartment = detectCompartment(Set(innerTiles.head))
    detectAllCompartments(innerTiles -- compartment.innerTiles, current + compartment)
  }

  private def detectCompartment(toProcess: Set[Tile]): WallCompartment = {
    detectCompartment(toProcess, Set.empty, Set.empty)
  }

  private def detectCompartment(toProcess: Set[Tile], currentInnerTiles: Set[Tile], currentWalls: Set[Wall]): WallCompartment = {
    if (toProcess.isEmpty) return WallCompartment(currentWalls, currentInnerTiles)
    val notSeenTouching = toProcess.head
      .touchingTiles
      .diff(currentInnerTiles)

    val touchingWalls = walls.filter(wall => notSeenTouching.contains(wall.block.tile))

    detectCompartment(
      toProcess.tail ++ notSeenTouching -- touchingWalls.map(_.block.tile),
      currentInnerTiles + toProcess.head,
      currentWalls ++ touchingWalls
    )
  }
}

object Village {
  val empty = Village(Set.empty)
}