package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class BarbarianKing(level: PosInt, tile: Tile) extends Hero {
  lazy val range = CircularElementRange(block.centre, 8)
  val targets = Set(Target.Ground)
  val size: PosInt = 3
}
