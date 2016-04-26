package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class AirDefense(level: PosInt, tile: Tile) extends Defense {
  lazy val range = CircularElementRange(block.centre, 10)
  val targets = Target.AirOnly
  val size = PosInt(3)
}
