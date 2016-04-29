package org.danielholmes.coc.baseanalyser.model.trash

import org.danielholmes.coc.baseanalyser.model.{PreventsTroopDrop, Building, Tile}
import org.scalactic.anyvals.PosInt

case class ArmyCamp(level: PosInt, tile: Tile) extends Building with PreventsTroopDrop {
  val size: PosInt = 5
  override lazy val hitSize: PosInt = 3
}
