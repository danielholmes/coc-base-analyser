package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.{TileCoordinate, Element, ClanCastle, Village}

class HogCCLureRule extends Rule {
  def analyse(village: Village): RuleResult = {
    HogCCLureFail(
      village.attackPlacementCoordinates
        .flatMap(c => closestCCRadiusPathing(c, village))
        .toSet
    )
  }

  private def closestCCRadiusPathing(coordinate: TileCoordinate, village: Village): Option[(Element, TileCoordinate)] = {
    // TODO: cover in helper method on village when work out typing
    village.elements
      .find(_.isInstanceOf[ClanCastle])
      .flatMap(_ => hogTargetHitPoint(coordinate, village))
  }

  private def hogTargetHitPoint(dropPoint: TileCoordinate, village: Village): Option[(Element, TileCoordinate)] = {
    None
  }

  //private def closestDefense
}

case class HogCCLureFail(dropLocations: Set[(Element, TileCoordinate)]) extends RuleResult {
  val success: Boolean = dropLocations.isEmpty
}
