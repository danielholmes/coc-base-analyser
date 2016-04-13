package org.danielholmes.coc.baseanalyser.model

case class WallCompartment(walls: Set[Wall], innerTiles: Set[Tile]) {
  //require(walls.nonEmpty) For a non walled village, you could consider it a large wall compartment?
  require(innerTiles.nonEmpty)

  private lazy val allTiles = innerTiles ++ walls.map(_.block.tile)

  def contains(tile: Tile) = allTiles.contains(tile)
}