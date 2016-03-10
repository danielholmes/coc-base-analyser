package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, Archer, HogTargeting, HogRider}

class ArcherAnchorRule extends Rule {
  def analyse(village: Village): RuleResult = {
    if (village.isEmpty) return RuleResult.success(name)
    val groundDefenses = village.elements
      .filter(_.isInstanceOf[Defense])
      .map(_.asInstanceOf[Defense])
      .filter(_.targets.contains(Target.Ground))
    ArcherAnchorRuleResult(
      name,
      village.coordinatesAllowedToDropTroop
        .flatMap(Archer.findTarget(_, village))
        .filter(targeting => !groundDefenses.exists(_.range.contains(targeting.standingPosition)))
    )
  }

  private val name: String = "ArcherAnchor"
}

case class ArcherAnchorRuleResult(ruleName: String, targeting: Set[ArcherTargeting]) extends RuleResult {
  val success = targeting.isEmpty
}
