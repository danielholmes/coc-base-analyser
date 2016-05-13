package org.danielholmes.coc.baseanalyser.model.traps

import org.danielholmes.coc.baseanalyser.model.Tile
import org.scalactic.anyvals.PosInt

case class SkeletonTrap(level: PosInt, tile: Tile) extends Trap {
  val size = PosInt(1)
}
