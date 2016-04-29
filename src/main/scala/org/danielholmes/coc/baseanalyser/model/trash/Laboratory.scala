package org.danielholmes.coc.baseanalyser.model.trash

import org.danielholmes.coc.baseanalyser.model.{PreventsTroopDrop, Tile, Building}
import org.scalactic.anyvals.PosInt

case class Laboratory(level: PosInt, tile: Tile) extends Building with PreventsTroopDrop {
  val size = PosInt(4)
}
