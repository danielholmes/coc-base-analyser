package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class ClanCastle(level: PosInt, tile: Tile) extends Building {
  val size = PosInt(3)

  lazy val range = CircularElementRange(block.centre, 12)
}
