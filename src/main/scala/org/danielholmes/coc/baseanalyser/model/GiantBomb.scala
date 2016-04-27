package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class GiantBomb(level: PosInt, tile: Tile) extends Trap {
  val size = PosInt(2)
}
