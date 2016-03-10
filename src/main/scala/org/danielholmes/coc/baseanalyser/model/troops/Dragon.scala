package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, TileCoordinate, TileSize}

object Dragon {
  def getCoordinatesCanAttackElementFrom(element: Element): Set[TileCoordinate] = {
    element.block
      .expandBy(TileSize(1))
      .coordinates
  }
}
