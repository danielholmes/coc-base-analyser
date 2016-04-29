package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Structure, TileCoordinate}

case class ArcherTargeting(standingPosition: TileCoordinate, targeting: Structure) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(standingPosition)
}
