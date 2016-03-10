package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, TileCoordinate}

case class ArcherTargeting(standingPosition: TileCoordinate, targeting: Element) {
  lazy val hitPoint = targeting.findClosestHitCoordinate(standingPosition)
}
