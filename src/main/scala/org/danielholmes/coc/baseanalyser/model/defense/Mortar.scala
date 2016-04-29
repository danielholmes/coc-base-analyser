package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.BlindSpotCircularElementRange
import org.danielholmes.coc.baseanalyser.model.{StationaryDefensiveBuilding, PreventsTroopDrop, Target, Tile}
import org.scalactic.anyvals.PosInt

case class Mortar(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  lazy val range = BlindSpotCircularElementRange(block.centre, 4, 11)
  val size = PosInt(3)
  val targets = Target.GroundOnly
}
