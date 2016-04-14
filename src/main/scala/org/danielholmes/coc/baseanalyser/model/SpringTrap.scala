package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class SpringTrap(level: PosInt, tile: Tile) extends Trap {
  val size: PosInt = 1
}
