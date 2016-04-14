package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class ArmyCamp(level: PosInt, tile: Tile) extends Building {
  val size: PosInt = 5
  override lazy val hitSize: PosInt = 3
}
