package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.model.defense.AirDefense
import org.danielholmes.coc.baseanalyser.model.special.{ClanCastle, TownHall}
import org.scalactic.anyvals.PosInt

import scala.annotation.tailrec
import scala.reflect.{ClassTag, classTag}

case class Village(elements: Set[Element]) {
  private val firstIntersect = Block.firstIntersecting(elements.map(_.block))
  require(
    firstIntersect.isEmpty,
    s"Elements musn't overlap (currently $firstIntersect overlaps"
  )

  // It's a real world base, it has a town hall level. Maybe this should be a requirement? Only reason doesnt exist is
  // for testing which is a poor excuse to harm data model
  val townHallLevel = findElementByType[TownHall].map(_.level)

  val clanCastle = findElementByType[ClanCastle]

  lazy val isEmpty = elements.isEmpty

  lazy val structures = getElementsByType[Structure]

  lazy val preventsTroopDropStructures = getElementsByType[PreventsTroopDrop]

  lazy val stationaryDefensiveBuildings = getElementsByType[StationaryDefensiveBuilding]

  lazy val defenses = getElementsByType[Defense]

  lazy val groundTargetingDefenses = defenses.filter(_.targets.contains(Target.Ground))

  lazy val airDefenses = getElementsByType[AirDefense]

  lazy val buildings = getElementsByType[Building]


  private lazy val tilesNotAllowedToDropTroop = preventsTroopDropStructures.flatMap(_.preventTroopDropBlock.tiles)

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

  // TODO: Need to consider channel bases
  lazy val possibleInternalLargeTraps = wallCompartments.flatMap(_.possibleLargeTraps)

  lazy val wallTiles = walls.map(_.block.tile)

  private lazy val walls = getElementsByType[Wall]

  lazy val outerTiles: Set[Tile] = detectCompartment(Tile.AllOutsideMap).innerTiles

  lazy val outerTileCoordinates: Set[TileCoordinate] = outerTiles.flatMap(_.allCoordinates)

  private def getElementsByType[T: ClassTag] =
    elements.filter(classTag[T].runtimeClass.isInstance(_)).map(_.asInstanceOf[T])

  private def findElementByType[T: ClassTag] =
    elements.find(classTag[T].runtimeClass.isInstance(_)).map(_.asInstanceOf[T])

  @tailrec
  private def detectAllCompartments(innerTiles: Set[Tile], current: Set[WallCompartment]): Set[WallCompartment] = {
    innerTiles.toList match {
      case Nil => current
      case head :: tail =>
        val compartment = detectCompartment(Set(head))
        detectAllCompartments(innerTiles -- compartment.innerTiles, current + compartment)
    }
  }

  private def detectCompartment(toProcess: Set[Tile]): WallCompartment = {
    detectCompartment(toProcess, Set.empty, Set.empty)
  }

  @tailrec
  private def detectCompartment(toProcess: Set[Tile], currentInnerTiles: Set[Tile], currentWalls: Set[Wall]): WallCompartment = {
    toProcess.toList match {
      case Nil => WallCompartment(currentWalls, currentInnerTiles, elements.filter(e => currentInnerTiles.contains(e.block.tile)))
      case head :: tail =>
        val notSeenTouching = head.touchingTiles.diff(currentInnerTiles)
        val touchingWalls = walls.filter(wall => notSeenTouching.contains(wall.block.tile))
        detectCompartment(
          toProcess.tail ++ notSeenTouching -- touchingWalls.map(_.block.tile),
          currentInnerTiles + toProcess.head,
          currentWalls ++ touchingWalls
        )
    }
  }
}

object Village {
  val empty = Village(Set.empty)
}
