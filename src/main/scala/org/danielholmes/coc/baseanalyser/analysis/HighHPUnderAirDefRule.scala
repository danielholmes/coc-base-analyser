package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.Dragon

import scala.annotation.tailrec

class HighHPUnderAirDefRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val airDefs = village.elements
      .filter(_.isInstanceOf[AirDefense])
      .map(_.asInstanceOf[AirDefense])
    val highHPBuildings = village.elements.filter(isHighHPBuilding)
    val covered = highHPBuildings.partition(willSomeAirDefCoverDragonShooting(_, airDefs))
    HighHPUnderAirDefRuleResult(covered._2, covered._1)
  }

  private def willSomeAirDefCoverDragonShooting(highHP: Element, airDefs: Set[AirDefense]): Boolean = {
    willSomeAirDefCoverDragonShooting(Dragon.getCoordinatesCanAttackElementFrom(highHP).toList, airDefs)
  }

  @tailrec
  private def willSomeAirDefCoverDragonShooting(highHPCoords: List[TileCoordinate], airDefs: Set[AirDefense]): Boolean = {
    highHPCoords match {
      case Nil => true
      case head :: tail => airDefs.exists(_.range.contains(head)) && willSomeAirDefCoverDragonShooting(tail, airDefs)
    }
  }

  private def isHighHPBuilding(element: Element): Boolean = {
    element match {
      case _: GoldStorage => true
      case _: DarkElixirStorage => true
      case _: ElixirStorage => true
      case _: TownHall => true
      case _: ClanCastle => true
      case _ => false
    }
  }
}

case class HighHPUnderAirDefRuleResult(outOfAirDefRange: Set[Element], inAirDefRange: Set[Element]) extends RuleResult {
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
