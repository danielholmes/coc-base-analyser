package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class Cannon(level: PosInt, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, 10)
  val size: PosInt = 3
  val targets = Set(Target.Ground)
}
