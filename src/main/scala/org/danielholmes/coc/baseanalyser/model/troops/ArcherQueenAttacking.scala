package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{PreventsTroopDrop, TileCoordinate}

case class ArcherQueenAttacking(startPosition: TileCoordinate, targeting: PreventsTroopDrop) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(startPosition)

  lazy val distance = startPosition.distanceTo(hitPoint)
}
