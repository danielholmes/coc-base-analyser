package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village
import org.scalactic.anyvals.PosZDouble

class EnoughPossibleTrapLocationsRule extends Rule {
  def analyse(village: Village): RuleResult = {
    EnoughPossibleTrapLocationsRuleResult(calculateScore(village))
  }

  def calculateScore(village: Village): PosZDouble = {
    PosZDouble.from(
      village.possibleInternalLargeTraps
        .flatMap(_.tiles)
        .groupBy(tile => village.possibleInternalLargeTraps.count(trap => trap.tiles.contains(tile)))
        .mapValues(_.size)
        .map({
          case (1, c) => EnoughPossibleTrapLocationsRule.TileScore * c
          case (_, c) => EnoughPossibleTrapLocationsRule.MultiUseTileScore * c
        })
        .sum
    ).get
  }
}

case class EnoughPossibleTrapLocationsRuleResult(score: PosZDouble) extends RuleResult {
  val success = score >= EnoughPossibleTrapLocationsRule.MinScore
  val minScore = EnoughPossibleTrapLocationsRule.MinScore
  val ruleDetails = EnoughPossibleTrapLocationsRule.Details
}

object EnoughPossibleTrapLocationsRule {
  val TileScore = 0.25

  val MultiUseTileScore = TileScore * 0.8

  val MinScore = 22

  val Details = RuleDetails(
    "EnoughPossibleTrapLocations",
    "Enough Possible Traps",
    "Enough Possible Trap Locations",
    "As well as real trap locations, your base should provide enough decoy locations to keep the attacker unsure and guessing about where the real traps are"
  )
}
