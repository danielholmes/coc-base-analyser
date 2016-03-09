package org.danielholmes.coc.baseanalyser.model

trait Element {
  require(level >= 1, s"level $level should be >= 1")

  val level: Int

  protected val tile: Tile
  val size: TileSize
  lazy val block = Block(tile, size)

  lazy val attackPlacementSize: TileSize = size + 2
  lazy val preventTroopDropBlock = Block.Map.createWithin(tile, -1, -1, attackPlacementSize)

  lazy val hitSize: TileSize = size
  lazy val hitBlock = Block(tile, hitSize)

  def findClosestHitCoordinate(from: MapTileCoordinate): MapTileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}