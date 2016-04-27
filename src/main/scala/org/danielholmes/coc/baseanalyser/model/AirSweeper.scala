package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

// TODO: Figure out what type of building it is exactly, not a defense like others
case class AirSweeper(level: PosInt, tile: Tile) extends Building {
  val size = PosInt(2)
}
