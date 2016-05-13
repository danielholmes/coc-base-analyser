package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

case class Decoration(tile: Tile) extends Element {
  lazy val level = PosInt(1)
  lazy val size = PosInt(2)
}
