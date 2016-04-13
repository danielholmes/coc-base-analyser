package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{HogTargeting, HogRider}

// TODO: Consider a channel through a base - currently thinks thats an exposed bk
class BKSwappableRule extends Rule {
  def analyse(village: Village): RuleResult = {
    BKSwappableRuleResult(
      village.elements
        .find(_.isInstanceOf[BarbarianKing])
        .map(_.asInstanceOf[BarbarianKing])
        .map(_.range)
        .map(_.allTouchingTiles)
        .getOrElse(Set.empty)
        .filter(outsideCompartmentOrInEmpty(village, _))
    )
  }

  private def outsideCompartmentOrInEmpty(village: Village, tile: Tile) = {
    village.wallCompartments.forall(!_.contains(tile)) || village.tilesAllowedToDropTroop.contains(tile)
  }
}

case class BKSwappableRuleResult(exposedTiles: Set[Tile]) extends RuleResult {
  val success = exposedTiles.isEmpty
}
