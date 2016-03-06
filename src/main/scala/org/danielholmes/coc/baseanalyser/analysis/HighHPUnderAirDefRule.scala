package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model._

class HighHPUnderAirDefRule extends Rule {
  def analyse(village: Village): RuleResult = {
    val airDefs = village.elements
      .filter(_.isInstanceOf[AirDefense])
      .map(_.asInstanceOf[AirDefense])
    if (airDefs.isEmpty) return RuleResult.pass

    val highHPBuildings = village.elements.filter(isHighHPBuilding)
    HighHPUnderAirDefResult(highHPBuildings.filterNot(willAirDefShootWhileDragAttacking(_, airDefs)))
  }

  private def willAirDefShootWhileDragAttacking(highHP: Element, airDefs: Set[AirDefense]): Boolean = {
    if (airDefs.isEmpty) return false
    airDefs.head.range.containsAll(Dragon.getCoordinatesCanAttackElementFrom(highHP)) ||
      willAirDefShootWhileDragAttacking(highHP, airDefs.tail)
  }

  private def isHighHPBuilding(element: Element): Boolean = {
    element match {
      case _: GoldStorage => true
      case _: DarkElixirStorage => true
      case _: ElixirStorage => true
      case _: TownHall => true
      case _ => false
    }
  }
}

case class HighHPUnderAirDefResult(outOfAirDefRange: Set[Element]) extends RuleResult {
  val success = outOfAirDefRange.isEmpty
}
