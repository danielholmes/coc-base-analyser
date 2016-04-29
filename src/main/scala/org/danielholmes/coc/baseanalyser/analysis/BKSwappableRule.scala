package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.{PosDouble, PosZDouble}

import scala.annotation.tailrec

class BKSwappableRule extends Rule {
  private val CloseEnoughFromDropToSwap = PosDouble(5.0)
  private val MinExposedDistance = PosDouble(1.25)

  def analyse(village: Village): RuleResult = {
    val exposedTiles = findExposedTiles(village)
    BKSwappableRuleResult(
      findTouchingTiles(
        findTriggerTiles(village, exposedTiles).toList,
        exposedTiles,
        Set.empty
      )
    )
  }

  @tailrec
  private def findTouchingTiles(touchingTrigger: List[Tile], exposedToCheck: Set[Tile], current: Set[Tile]): Set[Tile] = {
    touchingTrigger match {
      case Nil => current
      case head :: tail =>
        val exposedTouchingTrigger = touchingTrigger.head.touchingTiles.intersect(exposedToCheck)
        findTouchingTiles(
          (touchingTrigger.toSet ++ exposedTouchingTrigger -- current).toList,
          exposedToCheck -- exposedTouchingTrigger,
          current + head
        )
    }
  }

  private def findTriggerTiles(village: Village, exposedTiles: Set[Tile]) = {
    village.elements
      .find(_.isInstanceOf[BarbarianKing])
      .map(_.asInstanceOf[BarbarianKing])
      .map(_.range)
      .map(_.inset(MinExposedDistance))
      .map(_.allTouchingTiles)
      .getOrElse(Set.empty)
      .intersect(exposedTiles)
  }

  private def findExposedTiles(village: Village) = {
    village.elements
      .find(_.isInstanceOf[BarbarianKing])
      .map(_.asInstanceOf[BarbarianKing])
      .map(_.range)
      .map(_.allTouchingTiles)
      .getOrElse(Set.empty)
      .filter(inHoleOrOutsideCompartmentAndCloseEnoughToDrop(village, _))
  }

  private def inHoleOrOutsideCompartmentAndCloseEnoughToDrop(village: Village, tile: Tile) = {
    village.tilesAllowedToDropTroop.contains(tile) ||
      (village.wallCompartments.forall(!_.contains(tile)) && closestDropDistance(village, tile) <= CloseEnoughFromDropToSwap)
  }

  private def closestDropDistance(village: Village, tile: Tile): PosZDouble = {
    // Currently hangs, likely something wrong in a star heuristic
    /*village.tilesAllowedToDropTroop
      .flatMap(_.shortestTilePathTo(tile, village.wallTiles))
      .map(_.size)
      .min*/

    village.tilesAllowedToDropTroop
      .map(_.distanceTo(tile))
      .min
  }
}

case class BKSwappableRuleResult(exposedTiles: Set[Tile]) extends RuleResult {
  val success = exposedTiles.isEmpty
  val ruleDetails = BKSwappableRule.Details
}

object BKSwappableRule {
  val Details = RuleDetails(
    "BKSwappable",
    "BK protected",
    "BK should be protected",
    "The BK's range should be inside walls so he can't be lureed out and killed early as part of a tanking BK or KS"
  )
}
