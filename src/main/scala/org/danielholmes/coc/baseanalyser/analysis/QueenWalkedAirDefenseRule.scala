package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{ArcherQueen, ArcherQueenAttacking}
import org.danielholmes.coc.baseanalyser.model.Village
import org.danielholmes.coc.baseanalyser.model.defense.AirDefense


class QueenWalkedAirDefenseRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val attackings: Set[ArcherQueenAttacking] = village.airDefenses
      .flatMap(el =>
        ArcherQueen.firstPossibleAttackingCoordinate(el, village.outerTileCoordinates)
          .map(ArcherQueenAttacking(_, el))
      )
    val targetings = attackings.map(_.targeting).map(_.asInstanceOf[AirDefense])
    val nonReachableAirDefs = village.airDefenses.diff(targetings)
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
