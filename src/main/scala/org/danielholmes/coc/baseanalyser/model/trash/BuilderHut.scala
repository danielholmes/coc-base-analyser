package org.danielholmes.coc.baseanalyser.model.trash

import org.danielholmes.coc.baseanalyser.model.{PreventsTroopDrop, Tile, Building}
import org.scalactic.anyvals.PosInt

case class BuilderHut(tile: Tile) extends Building with PreventsTroopDrop {
  val size = PosInt(2)
  val level = PosInt(1) // level not even relevant for builderhut
}
