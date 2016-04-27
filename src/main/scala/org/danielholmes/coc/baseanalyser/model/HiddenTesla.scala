package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class HiddenTesla(level: PosInt, tile: Tile) extends Defense {
  lazy val range = CircularElementRange(block.centre, 6)
  val targets = Target.Both
  val size = PosInt(2)
  override lazy val preventTroopDropBlock: Block = {
    throw new RuntimeException("Hidden teslas need refactor, need empty block or prevent troop drop as option")
  }
}
