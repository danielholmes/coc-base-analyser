package org.danielholmes.coc.baseanalyser.model.traps

import org.danielholmes.coc.baseanalyser.model.Tile
import org.scalactic.anyvals.PosInt

case class SpringTrap(tile: Tile) extends Trap {
  val size = PosInt(1)
  val level = PosInt(1) // level not even relevant
}
