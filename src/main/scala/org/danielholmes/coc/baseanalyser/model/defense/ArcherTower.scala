package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.{StationaryDefensiveBuilding, PreventsTroopDrop, Target, Tile}
import org.scalactic.anyvals.PosInt

case class ArcherTower(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  lazy val range = CircularElementRange(block.centre, 10)
  val targets = Target.Both
  val size = PosInt(3)
}
