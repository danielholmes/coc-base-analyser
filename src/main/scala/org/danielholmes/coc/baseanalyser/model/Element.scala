package org.danielholmes.coc.baseanalyser.model

trait Element {
  require(level >= 1, s"level $level should be >= 1")

  val level: Int

  val coordinate: TileCoordinate
  val size: TileSize
  def block = Block(coordinate, size)

  def attackPlacementSize: TileSize = size + 2
  def attackPlacementBlock = Block.Map.createWithin(coordinate, -1, -1, attackPlacementSize)

  def hitSize: TileSize = size
  def hitBlock = Block(coordinate, hitSize)

  def findClosestHitCoordinate(from: TileCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}