package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class ArcherTower(level: PosInt, tile: Tile) extends Defense {
  lazy val range = CircularElementRange(block.centre, 11)
  val targets = Target.Both
  val size = PosInt(3)
}
