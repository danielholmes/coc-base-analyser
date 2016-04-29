package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.AirDefense
import org.danielholmes.coc.baseanalyser.model.trash.{DarkElixirStorage, ElixirStorage, GoldStorage}
import org.danielholmes.coc.baseanalyser.model.troops.Dragon

import scala.annotation.tailrec

class HighHPUnderAirDefRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val highHPBuildings = village.structures.filter(isHighHPBuilding)
    val covered = highHPBuildings.partition(willSomeAirDefCoverDragonShooting(_, village.airDefenses))
    HighHPUnderAirDefRuleResult(covered._2, covered._1)
  }

  private def willSomeAirDefCoverDragonShooting(highHP: Structure, airDefs: Set[AirDefense]): Boolean = {
    willSomeAirDefCoverDragonShooting(Dragon.getAttackPositions(highHP), airDefs)
  }

  @tailrec
  private def willSomeAirDefCoverDragonShooting(highHPCoords: Set[MapCoordinate], airDefs: Set[AirDefense]): Boolean = {
    highHPCoords.toList match {
      case Nil => true
      case head :: tail => airDefs.exists(_.range.contains(head)) && willSomeAirDefCoverDragonShooting(tail.toSet, airDefs)
    }
  }

  private def isHighHPBuilding(structure: Structure): Boolean = {
    structure match {
      case _: GoldStorage => true
      case _: DarkElixirStorage => true
      case _: ElixirStorage => true
      case _: TownHall => true
      case _: ClanCastle => true
      case _ => false
    }
  }
}

case class HighHPUnderAirDefRuleResult(outOfAirDefRange: Set[Structure], inAirDefRange: Set[Structure]) extends RuleResult {
  require(outOfAirDefRange.intersect(inAirDefRange).isEmpty)

  val success = outOfAirDefRange.isEmpty
  val ruleDetails = HighHPUnderAirDefRule.Details
}

object HighHPUnderAirDefRule {
  val Details = RuleDetails(
    "HighHPUnderAirDef",
    "Air Covers High HP",
    "High HP covered by Air Defenses",
    "All high HP buildings should be within range of your air defenses"
  )
}
