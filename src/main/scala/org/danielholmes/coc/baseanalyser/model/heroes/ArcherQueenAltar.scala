package org.danielholmes.coc.baseanalyser.model.heroes

import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.{Target, Tile}
import org.scalactic.anyvals.PosInt

case class ArcherQueenAltar(level: PosInt, tile: Tile) extends HeroAltar {
  lazy val range = CircularElementRange(block.centre, 10)
  val targets = Target.Both
  val size = PosInt(3)
}
