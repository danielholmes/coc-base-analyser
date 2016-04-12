package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, Archer}

class ArcherAnchorRule extends Rule {
  def analyse(village: Village): RuleResult = {
    if (village.isEmpty) return ArcherAnchorRuleResult(Set.empty, Set.empty)
    val groundDefenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .map(_.asInstanceOf[Defense])
      .filter(_.targets.contains(Target.Ground))
    ArcherAnchorRuleResult(
      village.coordinatesAllowedToDropTroop
        .flatMap(Archer.findTarget(_, village))
        .filter(targeting => !groundDefenses.exists(_.range.contains(targeting.standingPosition))),
      groundDefenses
    )
  }
}

case class ArcherAnchorRuleResult(targeting: Set[ArcherTargeting], aimingDefenses: Set[Defense]) extends RuleResult {
  val ruleName: String = "ArcherAnchor"
  val success = targeting.isEmpty
}
