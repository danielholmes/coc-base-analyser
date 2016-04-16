package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class WizardTower(level: PosInt, tile: Tile) extends Defense {
  lazy val range = ElementRange(block.centre, 7)
  val targets = Target.Both
  val size = PosInt(3)
}
