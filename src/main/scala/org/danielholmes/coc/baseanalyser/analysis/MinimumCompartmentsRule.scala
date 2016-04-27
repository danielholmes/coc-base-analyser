package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.troops.{Minion, MinionAttackPosition}
import org.danielholmes.coc.baseanalyser.model.{Defense, Target, Village, WallCompartment}
import org.scalactic.anyvals.PosInt

class MinimumCompartmentsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    MinimumCompartmentsRuleResult(MinimumCompartmentsRule.Min, village.wallCompartments.filter(_.elements.nonEmpty))
  }
}

case class MinimumCompartmentsRuleResult(minimumCompartments: PosInt, buildingCompartments: Set[WallCompartment]) extends RuleResult {
  require(buildingCompartments.forall(_.elements.nonEmpty))

  val success = buildingCompartments.size >= minimumCompartments
  val ruleDetails = MinimumCompartmentsRule.Details
}

object MinimumCompartmentsRule {
  val Min = PosInt(8)

  val Details = RuleDetails(
    "MinimumCompartments",
    s">= ${Min.toInt} compartments",
    s"At least ${Min.toInt} compartments",
    "GoWiPe can be slowed down by having enough compartments (with buildings inside them) to hold it up"
  )
}
