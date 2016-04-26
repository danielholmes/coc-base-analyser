package org.danielholmes.coc.baseanalyser.model

import Target._
import org.danielholmes.coc.baseanalyser.model.XBowMode.XBowMode
import org.scalactic.anyvals.PosInt

case class XBow(level: PosInt, tile: Tile, private val mode: XBowMode) extends Defense {
  val targets = XBowMode.targets(mode)
  val size: PosInt = 3
  lazy val range = CircularElementRange(block.centre, XBowMode.radiusSize(mode))
}

object XBow {
  def ground(level: PosInt, tile: Tile): XBow = {
    XBow(level, tile, XBowMode.Ground) //Set(Target.Ground), 14)
  }

  def both(level: PosInt, tile: Tile): XBow = {
    XBow(level, tile, XBowMode.Both)
  }
}

object XBowMode extends Enumeration {
  type XBowMode = Value

  val Ground, Both = Value

  def targets(mode: XBowMode): Set[Target] = {
    if (mode == Ground) return Set(Target.Ground)
    Target.Both
  }

  def radiusSize(mode: XBowMode): PosInt = {
    if (mode == Ground) return 14
    11
  }
}
