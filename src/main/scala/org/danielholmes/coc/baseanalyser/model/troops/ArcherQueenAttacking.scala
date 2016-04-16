package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, TileCoordinate}

case class ArcherQueenAttacking(startPosition: TileCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)
}
