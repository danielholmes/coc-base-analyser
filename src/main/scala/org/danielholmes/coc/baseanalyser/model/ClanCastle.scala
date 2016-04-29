package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.scalactic.anyvals.PosInt

case class ClanCastle(level: PosInt, tile: Tile) extends PreventsTroopDrop {
  val size = PosInt(3)

  lazy val range = CircularElementRange(block.centre, 12)
}
