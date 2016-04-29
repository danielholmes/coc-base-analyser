package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class TownHall(level: PosInt, tile: Tile) extends PreventsTroopDrop {
  val size = PosInt(4)
}
