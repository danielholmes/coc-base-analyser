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
    ground.flatMap(bestNonAirCoveredAttackPosition(_, air))
  }

  private def bestNonAirCoveredAttackPosition(ground: Defense, air: Set[Defense]): Option[MinionAttackPosition] = {
    Minion.getAttackPositions(ground)
      .filter(coordinate => !air.exists(_.range.contains(coordinate)))
      .map(MinionAttackPosition(_, ground))
      .headOption
  }
}

case class AirSnipedDefenseRuleResult(snipedDefenses: Set[MinionAttackPosition], airDefenses: Set[Defense]) extends RuleResult {
  val success = snipedDefenses.isEmpty
}
