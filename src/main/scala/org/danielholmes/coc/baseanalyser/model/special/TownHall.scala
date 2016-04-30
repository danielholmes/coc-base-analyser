package org.danielholmes.coc.baseanalyser.model.special

import org.danielholmes.coc.baseanalyser.model.{Building, PreventsTroopDrop, Tile}
import org.scalactic.anyvals.PosInt

case class TownHall(level: PosInt, tile: Tile) extends Building with PreventsTroopDrop {
  val size = PosInt(4)
}
