package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait Structure extends Element {
  lazy val hitSize = size
  lazy val hitBlock = {
    if (hitSize == size) {
      block
    } else {
      val posOffset = PosInt.from((size - hitSize) / 2).get
      Block(tile.offset(posOffset, posOffset), hitSize)
    }
  }

  def findClosestHitCoordinate(from: FloatMapCoordinate): TileCoordinate = {
    hitBlock.findClosestCoordinate(from)
  }
}
