package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Block, _}
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

  def firstPossibleAttackingCoordinate(element: Element, coordinates: Set[TileCoordinate]): Option[TileCoordinate] = {
    firstPossibleAttackingCoordinate(element.block, element.block.border.toList, Set.empty, coordinates)
  }

  @tailrec
  private def firstPossibleAttackingCoordinate(
    block: Block,
    toProcess: List[TileCoordinate],
    processed: Set[TileCoordinate],
    allowed: Set[TileCoordinate]
  ): Option[TileCoordinate] = {
    toProcess match {
      case Nil => None
      case head :: tail =>
        if (allowed.contains(head)) {
          Some(head)
        } else {
          val unProcessedNeighboursWithinRange = head.neighbours.diff(processed)
            .filter(coord => block.distanceTo(coord) < Range)
          firstPossibleAttackingCoordinate(
            block,
            tail ::: unProcessedNeighboursWithinRange.toList,
            processed ++ unProcessedNeighboursWithinRange,
            allowed
          )
        }
    }
  }

  def getAttackFloatCoordinates(structure: Structure): Set[FloatMapCoordinate] = {
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

  def getAttackTileCoordinates(structure: Structure): Set[TileCoordinate] = {
    structure.hitBlock.topLeft.offset(-Range.toInt, -Range.toInt)
      .matrixOfCoordinatesTo(structure.hitBlock.bottomRight.offset(Range.toInt, Range.toInt))
      .filter(coord => structure.hitBlock.distanceTo(coord) <= Range)
  }

  def findReachableTargets(coordinate: TileCoordinate, village: Village): Set[Structure] = {
    findClosestTargets(getPrioritisedTargets(village), coordinate, (d: PosZDouble) => d < Range)
  }

  def getAllPossibleTargets(village: Village): Set[Structure] = {
    getPrioritisedTargets(village).flatMap(structures => structures).toSet
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
