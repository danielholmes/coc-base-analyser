package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class EagleArtillery(level: PosInt, tile: Tile) extends Defense {
  lazy val range = BlindSpotCircularElementRange(block.centre, 7, 50)
  val size = PosInt(4)
  val targets = Target.Both
}
