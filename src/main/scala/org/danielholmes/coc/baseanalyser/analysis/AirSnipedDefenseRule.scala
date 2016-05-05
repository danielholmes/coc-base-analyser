package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village}

class AirSnipedDefenseRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val defensesByAir = village.defenses
        .partition(_.targets.contains(Target.Air))

    AirSnipedDefenseRuleResult(
      noDefenseRangesCoverMinion(defensesByAir._2, defensesByAir._1),
      defensesByAir._1
    )
  }

  private def noDefenseRangesCoverMinion(ground: Set[Defense], air: Set[Defense]): Set[MinionAttackPosition] = {
    ground.flatMap(bestNonAirCoveredAttackPosition(_, air))
  }

  private def bestNonAirCoveredAttackPosition(ground: Defense, air: Set[Defense]): Option[MinionAttackPosition] = {
    Minion.getAttackFloatCoordinates(ground)
      .filter(coordinate => !air.exists(_.range.contains(coordinate)))
      .map(MinionAttackPosition(_, ground))
      .headOption
  }
}

case class AirSnipedDefenseRuleResult(snipedDefenses: Set[MinionAttackPosition], airDefenses: Set[Defense]) extends RuleResult {
  val success = snipedDefenses.isEmpty
  val ruleDetails = AirSnipedDefenseRule.Details
}

object AirSnipedDefenseRule {
  val Details = RuleDetails(
    "AirSnipedDefense",
    "Air covers Ground Defs",
    "Ground Defenses covered by Air",
    "No ground only defenses should be reachable by minions or loons"
  )
}
