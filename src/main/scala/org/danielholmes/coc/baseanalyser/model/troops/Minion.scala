package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element}

object Minion {
  def getAttackPositions(element: Element): Set[MinionAttackPosition] = {
    element.hitBlock.expandBy(1).allCoordinates.map(MinionAttackPosition(_, element))
  }
}
