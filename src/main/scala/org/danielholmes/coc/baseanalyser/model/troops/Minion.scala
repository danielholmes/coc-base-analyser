package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, TileSize}

object Minion {
  def getAttackPositions(element: Element): Set[MinionAttackPosition] = {
    element.hitBlock.expandBy(TileSize(1)).allCoordinates.map(MinionAttackPosition(_, element))
  }
}
