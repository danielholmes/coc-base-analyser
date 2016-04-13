package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._

// TODO: Consider a channel through a base - currently thinks thats an exposed bk
class BKSwappableRule extends Rule {
  val CloseEnoughToSwap = 5.0

  def analyse(village: Village): RuleResult = {
    BKSwappableRuleResult(
      village.elements
        .find(_.isInstanceOf[BarbarianKing])
        .map(_.asInstanceOf[BarbarianKing])
        .map(_.range)
        .map(_.allTouchingTiles)
        .getOrElse(Set.empty)
        .filter(inHoleOrOutsideCompartmentAndCloseEnoughToDrop(village, _))
    )
  }

  private def inHoleOrOutsideCompartmentAndCloseEnoughToDrop(village: Village, tile: Tile) = {
    village.tilesAllowedToDropTroop.contains(tile) ||
      (village.wallCompartments.forall(!_.contains(tile)) && closestDropDistance(village, tile) <= CloseEnoughToSwap)
  }

  private def closestDropDistance(village: Village, tile: Tile): Double = {
    /* village.tilesAllowedToDropTroop
      .flatMap(_.shortestTilePathTo(tile, village.wallTiles))
      .map(_.size)
      .min */

    village.tilesAllowedToDropTroop
      .map(_.distanceTo(tile))
      .min
  }
}

case class BKSwappableRuleResult(exposedTiles: Set[Tile]) extends RuleResult {
  val success = exposedTiles.isEmpty
}
