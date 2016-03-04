package org.danielholmes.coc.baseanalyser.model

trait Element {
  require(level >= 1, s"level $level should be >= 1")

  val level: Int

  val coordinate: TileCoordinate
  val size: TileSize
  lazy val block = Block(coordinate, size)

  lazy val attackPlacementSize: TileSize = size + 2
  lazy val attackPlacementBlock = Block.Map.createWithin(coordinate, -1, -1, attackPlacementSize)

  lazy val hitSize: TileSize = size
  lazy val hitBlock = Block(coordinate, hitSize)

  def findClosestHitCoordinate(from: TileCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}