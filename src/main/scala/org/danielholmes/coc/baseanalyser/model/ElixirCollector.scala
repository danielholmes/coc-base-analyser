package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class ElixirCollector(level: PosInt, tile: Tile) extends Building {
  val size: PosInt = 3
}
