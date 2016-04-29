package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Structure, TileCoordinate, Village}
import org.scalactic.anyvals.{PosInt, PosZDouble, PosZInt}

import scala.annotation.tailrec

trait Troop {
  val Range: PosZDouble

  protected def getPrioritisedTargets(village: Village): List[Set[Structure]]

  protected def getDefenseTargetingTargets(village: Village): List[Set[Structure]] = {
    List(
      village.stationaryDefensiveBuildings.toSet[Structure],
      (village.buildings -- village.stationaryDefensiveBuildings).toSet[Structure]
    )
  }

  protected def getAnyBuildingsTargets(village: Village): List[Set[Structure]] = {
    List(village.buildings.map(_.asInstanceOf[Structure]))
  }

  def getAttackPositions(structure: Structure): Set[MapCoordinate] = {
    val diagonalRange = Math.sqrt(Range / 2)
    structure.hitBlock.allCoordinates.map(TileCoordinate.widenToMapCoordinate) ++
      structure.hitBlock.leftSide.map(_.offset(-Range, 0)) ++
      structure.hitBlock.rightSide.map(_.offset(Range, 0)) ++
      structure.hitBlock.topSide.map(_.offset(0, -Range)) ++
      structure.hitBlock.bottomSide.map(_.offset(0, Range)) +
      structure.hitBlock.topLeft.offset(-diagonalRange, -diagonalRange) +
      structure.hitBlock.topRight.offset(diagonalRange, -diagonalRange) +
      structure.hitBlock.bottomLeft.offset(-diagonalRange, diagonalRange) +
      structure.hitBlock.bottomRight.offset(diagonalRange, diagonalRange)
  }

  def findReachableTargets(coordinate: TileCoordinate, village: Village): Set[Structure] = {
    findClosestTargets(getPrioritisedTargets(village), coordinate, (d: PosZDouble) => d < Range)
  }

  def findTargets(coordinate: TileCoordinate, village: Village): Set[Structure] = {
    findClosestTargets(getPrioritisedTargets(village), coordinate, (d: PosZDouble) => true)
  }

  @tailrec
  private def findClosestTargets(targets: List[Set[Structure]], coordinate: TileCoordinate, distanceFilter: PosZDouble => Boolean): Set[Structure] = {
    targets match {
      case Nil => Set.empty
      case head :: tail =>
        val setTargets = findClosestTargets(head, coordinate, distanceFilter)
        setTargets.toList match {
          case Nil => findClosestTargets(tail, coordinate, distanceFilter)
          case result: List[Structure] => result.toSet
        }
    }
  }

  private def findClosestTargets(targets: Set[Structure], coordinate: TileCoordinate, distanceFilter: PosZDouble => Boolean): Set[Structure] = {
    targets.groupBy(_.hitBlock.distanceTo(coordinate))
      .toSeq
      .filter(t => distanceFilter(t._1))
      .sortBy(_._1)
      .headOption
      .map(_._2)
      .getOrElse(Set.empty)
  }
}
