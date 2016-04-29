package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village
import org.danielholmes.coc.baseanalyser.model.heroes.ArcherQueenAltar
import org.scalactic.anyvals.PosInt

class QueenWontLeaveCompartmentRule extends Rule {
  def analyse(village: Village): RuleResult = {
    village.elements
      .find(_.isInstanceOf[ArcherQueenAltar])
      .map(queen =>
        village.wallCompartments
          .find(_.elements.contains(queen))
          .filter(c => queen.block.expandBy(QueenWontLeaveCompartmentRule.MinClearance).tiles.subsetOf(c.innerTiles))
          .map(q => QueenWontLeaveCompartmentRuleResult(true))
          .getOrElse(QueenWontLeaveCompartmentRuleResult(false))
      )
      .getOrElse(QueenWontLeaveCompartmentRuleResult(true))
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
