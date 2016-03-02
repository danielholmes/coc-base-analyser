package org.danielholmes.coc.baseanalyser.model

trait Element {
  val level: Int

  val coordinate: TileCoordinate
  val size: TileSize
  def block = Block(coordinate, size)

  val attackPlacementSize: TileSize = size

  val hitSize: TileSize = size
  def hitBlock = Block(coordinate, hitSize)

  // out of constructor so dont work
  /*require(level >= 1, "level should be >= 1")
  require(block.oppositeX < TileCoordinate.Max, "Outside coords")
  require(block.oppositeY < TileCoordinate.Max, "Outside coords")*/

  def findClosestHitCoordinate(from: TileCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}