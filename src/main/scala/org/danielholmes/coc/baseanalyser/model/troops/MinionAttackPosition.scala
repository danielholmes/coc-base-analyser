package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{MapCoordinate, Structure}

case class MinionAttackPosition(startPosition: MapCoordinate, targeting: Structure) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)
}
