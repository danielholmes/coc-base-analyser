package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, Archer}

// TODO: Shouldn't take into account EagleArtillery since wont be activated. Test this
class ArcherAnchorRule extends Rule {
  def analyse(village: Village): RuleResult = {
    if (village.isEmpty) return ArcherAnchorRuleResult(Set.empty, Set.empty)
    val groundDefenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .map(_.asInstanceOf[Defense])
      .filter(_.targets.contains(Target.Ground))
    ArcherAnchorRuleResult(
      village.coordinatesAllowedToDropTroop
        .flatMap(Archer.findTargets(_, village))
        .filter(targeting => !groundDefenses.exists(_.range.contains(targeting.standingPosition))),
      groundDefenses
    )
  }
}

case class ArcherAnchorRuleResult(targeting: Set[ArcherTargeting], aimingDefenses: Set[Defense]) extends RuleResult {
  val success = targeting.isEmpty
  val ruleDetails = ArcherAnchorRule.Details
}

object ArcherAnchorRule {
  val Details = RuleDetails(
    "ArcherAnchor",
    "No Arch Anchors",
    "No Archer Anchors",
    "There should be no unprotected archer anchors"
  )
}
