package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village, WallCompartment}
import org.scalactic.anyvals.PosInt

// TODO: Should only count compartments with buildings inside (since they're the only ones that need to be broken)
// Also update rule text to reflect this
class MinimumCompartmentsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    MinimumCompartmentsRuleResult(MinimumCompartmentsRule.Min, village.wallCompartments)
  }
}

case class MinimumCompartmentsRuleResult(minimumCompartments: PosInt, compartments: Set[WallCompartment]) extends RuleResult {
  val success = compartments.size >= minimumCompartments
  val ruleDetails = MinimumCompartmentsRule.Details
}

object MinimumCompartmentsRule {
  val Min = PosInt(8)

  val Details = RuleDetails(
    "MinimumCompartments",
    s">= ${Min.toInt} compartments",
    s"At least ${Min.toInt} compartments",
    "GoWiPe can be slowed down by having enough compartments to hold it up"
  )
}
