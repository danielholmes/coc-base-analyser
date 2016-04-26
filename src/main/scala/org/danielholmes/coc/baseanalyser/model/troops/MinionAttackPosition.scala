package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Element}

case class MinionAttackPosition(startPosition: MapCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)
}