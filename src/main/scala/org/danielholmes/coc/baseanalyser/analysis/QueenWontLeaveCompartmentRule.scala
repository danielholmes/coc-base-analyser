package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.{ArcherQueen, Village}
import org.scalactic.anyvals.PosInt

class QueenWontLeaveCompartmentRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val queen = village.elements.find(_.isInstanceOf[ArcherQueen])
    if (queen.isEmpty) return QueenWontLeaveCompartmentRuleResult(true)

    village.wallCompartments
      .find(_.elements.contains(queen.get))
      .filter(c => queen.get.block.expandBy(QueenWontLeaveCompartmentRule.MinClearance).tiles.subsetOf(c.innerTiles))
      .map(q => QueenWontLeaveCompartmentRuleResult(true))
      .getOrElse(QueenWontLeaveCompartmentRuleResult(false))
  }
}

case class QueenWontLeaveCompartmentRuleResult(success: Boolean) extends RuleResult {
  val ruleDetails = QueenWontLeaveCompartmentRule.Details
}

object QueenWontLeaveCompartmentRule {
  val MinClearance = PosInt(3)

  val Details = RuleDetails(
    "QueenWontLeaveCompartment",
    "AQ in >= 9x9",
    "Archer Queen wont leave compartment",
    "Your Archer Queen should be within a compartment large enough so that she won't jump out (centre of 9x9)"
  )
}
