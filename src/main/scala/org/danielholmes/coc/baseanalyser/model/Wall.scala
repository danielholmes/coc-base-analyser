package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class Wall(level: PosInt, tile: Tile) extends PreventsTroopDrop {
  val size: PosInt = 1
}
