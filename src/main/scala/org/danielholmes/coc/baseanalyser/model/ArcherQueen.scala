package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class ArcherQueen(level: PosInt, tile: Tile) extends Hero {
  lazy val range = CircularElementRange(block.centre, 8)
  val targets = Target.Both
  val size = PosInt(3)
}
