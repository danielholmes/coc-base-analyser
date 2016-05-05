package org.danielholmes.coc.baseanalyser.model.special

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.{Building, PreventsTroopDrop, Tile}
import org.scalactic.anyvals.PosInt

case class ClanCastle(level: PosInt, tile: Tile) extends Building with PreventsTroopDrop {
  val size = PosInt(3)

  lazy val range = CircularElementRange(block.centre, 12.0)
}
