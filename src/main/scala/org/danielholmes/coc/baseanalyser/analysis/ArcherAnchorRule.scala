package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, Archer}

// TODO: Shouldn't take into account EagleArtillery since wont be activated. Test this
class ArcherAnchorRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val targetImmediateArcherGroundDefenses = village.groundTargetingDefenses
      .filter({
        case d: DelayedActivation => false
        case g: Defense => true
      })

    val safeCoords = village.coordinatesAllowedToDropTroop
      .filter(coord => !targetImmediateArcherGroundDefenses.exists(_.range.contains(coord)))
    val possibleTargets = Archer.getAllPossibleTargets(village)

    ArcherAnchorRuleResult(
      possibleTargets.flatMap(possibleTarget => {
        safeCoords.intersect(Archer.getAttackTileCoordinates(possibleTarget))
          .find(_ => true)
          .map(coord => ArcherTargeting(coord, possibleTarget))
      }),
      targetImmediateArcherGroundDefenses
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
