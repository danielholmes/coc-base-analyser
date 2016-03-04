package org.danielholmes.coc.baseanalyser.model

object Dragon {
  def getCoordinatesCanAttackElementFrom(element: Element): Set[TileCoordinate] = {
    element.block
      .expandWithinMap(TileSize(1))
      .allCoordinates
  }
}
