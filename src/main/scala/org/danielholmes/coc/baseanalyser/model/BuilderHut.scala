package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class BuilderHut(tile: Tile) extends Building {
  val size = PosInt(2)
  val level = PosInt(1) // level not even relevant for builderhut
}
