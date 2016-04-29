package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait Element {
  val level: PosInt

  protected val tile: Tile
  val size: PosInt
  lazy val block = Block(tile, size)
}
