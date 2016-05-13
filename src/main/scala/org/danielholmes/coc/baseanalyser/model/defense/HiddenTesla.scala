package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.scalactic.anyvals.PosInt

case class HiddenTesla(level: PosInt, tile: Tile) extends StationaryDefensiveBuilding with Hidden {
  lazy val range = CircularElementRange(block.centre, 7) // Note: Simplified, shows at 6, then 7
  val targets = Target.Both
  val size = PosInt(2)
}
