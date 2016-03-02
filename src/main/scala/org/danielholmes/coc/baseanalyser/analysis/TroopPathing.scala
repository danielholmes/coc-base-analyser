package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.{TileCoordinate, Element}

case class TroopPathing(val startPosition: TileCoordinate, val targeting: Element) {
  val hitPoint = targeting.findClosestHitCoordinate(startPosition)
}
