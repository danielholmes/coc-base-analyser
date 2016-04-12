package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village}

class AirSnipedDefenseRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val defensesByAir = village.elements
        .filter(_.isInstanceOf[Defense])
        .map(_.asInstanceOf[Defense])
        .partition(_.targets.contains(Target.Air))

    AirSnipedDefenseRuleResult(
      noDefenseRangesCoverMinion(defensesByAir._2, defensesByAir._1),
      defensesByAir._1
    )
  }

  private def noDefenseRangesCoverMinion(ground: Set[Defense], air: Set[Defense]): Set[MinionAttackPosition] = {
    ground.flatMap(nonAirCoveredAttackPositions(_, air))
  }

  private def nonAirCoveredAttackPositions(ground: Defense, air: Set[Defense]): Set[MinionAttackPosition] = {
    Minion.getAttackPositions(ground)
        .filter(position => !air.exists(_.range.contains(position.coordinate)))
  }
}

case class AirSnipedDefenseRuleResult(snipedDefenses: Set[MinionAttackPosition], airDefenses: Set[Defense]) extends RuleResult {
  val ruleName = "AirSnipedDefense"
  val success = snipedDefenses.isEmpty
}
