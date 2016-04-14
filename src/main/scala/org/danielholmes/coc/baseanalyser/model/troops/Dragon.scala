package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, TileCoordinate}

object Dragon {
  def getCoordinatesCanAttackElementFrom(element: Element): Set[TileCoordinate] = {
    element.hitBlock
      .expandBy(1)
      .allCoordinates
  }
}
