package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village, WallCompartment}

class MinimumCompartmentsRule extends Rule {
  val Min = 8

  def analyse(village: Village): RuleResult = {
    MinimumCompartmentsRuleResult(Min, village.wallCompartments)
  }
}

case class MinimumCompartmentsRuleResult(minimumCompartments: Int, compartments: Set[WallCompartment]) extends RuleResult {
  val ruleName = "MinimumCompartments"
  val success = compartments.size >= minimumCompartments
}
