package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait Element {
  val level: PosInt

  protected val tile: Tile
  val size: PosInt
  lazy val block = Block(tile, size)

  lazy val preventTroopDropSize: PosInt = PosInt.from(size + 2).get
  lazy val preventTroopDropBlock = block.expandToSize(preventTroopDropSize)

  // TODO: Hit size not relevant for non-traps, maybe need new heirarchy element
  lazy val hitSize: PosInt = size
  lazy val hitBlock = {
    if (hitSize == size) {
      block
    } else {
      val posOffset = PosInt.from((size - hitSize) / 2).get
      Block(tile.offset(posOffset, posOffset), hitSize)
    }
  }

  // TODO: Think this is wrong, see line drawn on dakota's war base
  def findClosestHitCoordinate(from: TileCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}