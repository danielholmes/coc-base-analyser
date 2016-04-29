package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.{StationaryDefensiveBuilding, PreventsTroopDrop, Target, Tile}
import org.scalactic.anyvals.PosInt

case class Cannon(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  lazy val range = CircularElementRange(block.centre, 9)
  val size = PosInt(3)
  val targets = Target.GroundOnly
}
