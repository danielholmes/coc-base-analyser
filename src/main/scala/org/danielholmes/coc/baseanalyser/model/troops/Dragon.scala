package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, MapTileCoordinate, TileSize}

object Dragon {
  def getCoordinatesCanAttackElementFrom(element: Element): Set[MapTileCoordinate] = {
    element.block
      .expandWithinMap(TileSize(1))
      .coordinates
  }
}
