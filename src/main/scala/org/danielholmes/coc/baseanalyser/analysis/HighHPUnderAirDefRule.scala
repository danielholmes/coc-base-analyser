package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.Dragon

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
    if (airDefs.isEmpty) return false
    willSomeAirDefCoverDragonShooting(
      Dragon.getCoordinatesCanAttackElementFrom(highHP),
      airDefs
    )
  }

  private def willSomeAirDefCoverDragonShooting(highHPCoords: Set[TileCoordinate], airDefs: Set[AirDefense]): Boolean = {
    if (highHPCoords.isEmpty) return true
    if (!airDefs.exists(_.range.contains(highHPCoords.head))) return false
    willSomeAirDefCoverDragonShooting(highHPCoords.tail, airDefs)
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
}
