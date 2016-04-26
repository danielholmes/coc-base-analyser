package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.{PosDouble, PosZDouble}

class BKSwappableRule extends Rule {
  private val CloseEnoughFromDropToSwap = PosDouble(5.0)
  private val MinExposedDistance = PosDouble(1.25)

  def analyse(village: Village): RuleResult = {
    val exposedTiles = findExposedTiles(village)
    if (exposedTiles.isEmpty) return BKSwappableRuleResult(Set.empty)
    val triggerTiles = findTriggerTiles(village, exposedTiles)
    if (triggerTiles.isEmpty) return BKSwappableRuleResult(Set.empty)
    BKSwappableRuleResult(findTouchingTiles(triggerTiles, exposedTiles, Set.empty))
  }

  private def findTouchingTiles(touchingTrigger: Set[Tile], exposedToCheck: Set[Tile], current: Set[Tile]): Set[Tile] = {
    if (exposedToCheck.isEmpty) return current ++ touchingTrigger
    if (touchingTrigger.isEmpty) return current
    val exposedTouchingTrigger = touchingTrigger.head.touchingTiles.intersect(exposedToCheck)
    findTouchingTiles(
      touchingTrigger.tail ++ exposedTouchingTrigger -- current,
      exposedToCheck -- exposedTouchingTrigger,
      current + touchingTrigger.head
    )
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
}
