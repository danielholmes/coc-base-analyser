package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{FloatMapCoordinate, Structure}

case class MinionAttackPosition(startPosition: FloatMapCoordinate, targeting: Structure) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)
}
