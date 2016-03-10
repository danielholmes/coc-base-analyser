package org.danielholmes.coc.baseanalyser.model

trait Element {
  require(level >= 1, s"level $level should be >= 1")
  // Doesnt work, size isn't available yet
  //require(block.isWithinMap, s"block $block must be within map")

  val level: Int

  protected val tile: Tile
  val size: TileSize
  lazy val block = Block(tile, size)

  lazy val preventTroopDropSize: TileSize = size + 2
  lazy val preventTroopDropBlock = block.expandToSize(preventTroopDropSize)

  lazy val hitSize: TileSize = size
  lazy val hitBlock = Block(tile, hitSize)

  def findClosestHitCoordinate(from: TileCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}