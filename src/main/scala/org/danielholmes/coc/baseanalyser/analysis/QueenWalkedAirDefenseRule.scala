package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{ArcherQueenAttacking, ArcherQueen}
import org.danielholmes.coc.baseanalyser.model.{AirDefense, Village}

class QueenWalkedAirDefenseRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val airDefenses = village.elements
      .filter(_.isInstanceOf[AirDefense])
      .map(_.asInstanceOf[AirDefense])

    val attackings: Set[ArcherQueenAttacking] = airDefenses.flatMap(el =>
      ArcherQueen.firstPossibleAttackingCoordinate(el, village.outerTileCoordinates)
          .map(ArcherQueenAttacking(_, el))
    )
    val nonReachableAirDefs = airDefenses.filterNot(a => attackings.exists(_.targeting == a))

    QueenWalkedAirDefenseRuleResult(attackings, nonReachableAirDefs)
  }
}

case class QueenWalkedAirDefenseRuleResult(attackings: Set[ArcherQueenAttacking], nonReachableAirDefs: Set[AirDefense]) extends RuleResult {
  val success = attackings.isEmpty
  val ruleDetails = QueenWalkedAirDefenseRule.Details
}

object QueenWalkedAirDefenseRule {
  val Details = RuleDetails(
    "QueenWalkedAirDefense",
    "AirDef not AQ Walkable",
    "Air Defenses not Queen Walkable",
    "Air Defenses shouldn't be reachable over a wall by a queen walking outside"
  )
}