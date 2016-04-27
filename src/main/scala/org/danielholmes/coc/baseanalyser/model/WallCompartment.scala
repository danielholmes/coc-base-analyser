package org.danielholmes.coc.baseanalyser.model

case class WallCompartment(walls: Set[Wall], innerTiles: Set[Tile], elements: Set[Element]) {
  //require(walls.nonEmpty) Currently use wall compartment to represent outer area in algorithms. Should change this
  require(innerTiles.nonEmpty)

  private lazy val allTiles = innerTiles ++ walls.map(_.block.tile)

  def contains(tile: Tile) = allTiles.contains(tile)
}