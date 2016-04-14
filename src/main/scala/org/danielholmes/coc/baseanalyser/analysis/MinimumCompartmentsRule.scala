package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village, WallCompartment}
import org.scalactic.anyvals.PosInt

class MinimumCompartmentsRule extends Rule {
  val Min = PosInt(8)

  def analyse(village: Village): RuleResult = {
    MinimumCompartmentsRuleResult(Min, village.wallCompartments)
  }
}

case class MinimumCompartmentsRuleResult(minimumCompartments: PosInt, compartments: Set[WallCompartment]) extends RuleResult {
  val success = compartments.size >= minimumCompartments
}
