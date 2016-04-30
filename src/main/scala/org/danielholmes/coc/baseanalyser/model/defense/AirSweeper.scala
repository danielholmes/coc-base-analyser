package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.WedgeElementRange
import org.danielholmes.coc.baseanalyser.model.{Target, PreventsTroopDrop, StationaryDefensiveBuilding, Tile}
import org.scalactic.anyvals.PosInt

// TODO: Figure out what type of building it is exactly, not a defense like others
case class AirSweeper(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  val size = PosInt(2)
  val range = WedgeElementRange(block.centre, 0, 15)
  val targets: Set[Target.Target] = Set.empty // Current way of disabling air sweeper for rules
}
