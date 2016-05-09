package org.danielholmes.coc.baseanalyser.analysis

import java.time.Duration

import org.danielholmes.coc.baseanalyser.model.{Tile, Village}
import org.danielholmes.coc.baseanalyser.util.TimedInvocation
import org.scalactic.anyvals.PosInt

class VillageAnalyser(private val rulesByThLevel: Map[PosInt, Set[Rule]]) {
  require(rulesByThLevel.nonEmpty)

  lazy val minTownHallLevel = rulesByThLevel.keys.min

  lazy val maxTownHallLevel = rulesByThLevel.keys.max

  def analyse(village: Village): Option[AnalysisReport] = {
    // NOTE: Times taken are thrown out if done in parallel
    village.townHallLevel
      .flatMap(rulesByThLevel.get)
      .map(analyse(_, village))
  }

  private def analyse(rules: Set[Rule], village: Village): AnalysisReport = {
    val buildingBlockTimes = runBuildingBlocks(village)
    buildReport(village, rules.map(rule => invokeRule(rule, village)), buildingBlockTimes)
  }

  private def runBuildingBlocks(village: Village): Map[String, Duration] = {
    // Walls?
    Seq(
      ("Tile Stuff", () => Tile.AllNotTouchingMap),
      ("Outer Tiles", () => village.outerTiles),
      ("Wall Compartments", () => village.wallCompartments),
      ("Allowed To Drop", () => village.coordinatesAllowedToDropTroop),
      ("Edge Prevent Drop", () => village.edgeOfHitCoordinatesAllowedToDropTroop)
    ).map(titleOp => (titleOp._1, TimedInvocation.run(titleOp._2)._2))
      .toMap
  }

  private def buildReport(
    village: Village,
    invocations: Set[(RuleResult, Duration)],
    buildingBlockTimes: Map[String, Duration]
  ): AnalysisReport = {
    AnalysisReport(
      village,
      invocations.map(_._1),
      AnalysisProfiling(
        buildingBlockTimes,
        invocations.map(i => (i._1.ruleDetails, i._2)).toMap
      )
    )
  }

  private def invokeRule(rule: Rule, village: Village): (RuleResult, Duration) = {
    TimedInvocation.run(() => rule.analyse(village))
  }
}
