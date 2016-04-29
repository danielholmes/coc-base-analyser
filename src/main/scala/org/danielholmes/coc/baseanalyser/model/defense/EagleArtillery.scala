package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.range.BlindSpotCircularElementRange
import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosInt

case class EagleArtillery(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with PreventsTroopDrop with DelayedActivation {
  lazy val range = BlindSpotCircularElementRange(block.centre, 7, 50)
  val deploymentSpaceRequired = PosInt(150)
  val size = PosInt(4)
  val targets = Target.Both
}
