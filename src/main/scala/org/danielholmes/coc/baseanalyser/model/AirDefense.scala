package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class AirDefense(level: PosInt, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, 10)
  val targets = Set(Target.Air)
  val size: PosInt = 3
}
