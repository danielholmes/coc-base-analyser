package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.BlindSpotSectorElementRange
import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosInt

case class AirSweeper(level: PosInt, tile: Tile, angle: AirSweeperAngle) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  val size = PosInt(2)
  val range = BlindSpotSectorElementRange(block.centre, 1.0, 15.0, angle.toAngle, Angle.degrees(120))
  val targets: Set[Target.Target] = Target.AirOnly
}
