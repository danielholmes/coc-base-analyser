package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class DarkElixirCollector(level: PosInt, tile: Tile) extends Building {
  val size = PosInt(3)
}
