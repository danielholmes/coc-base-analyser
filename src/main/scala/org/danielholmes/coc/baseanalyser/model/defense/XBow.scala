package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.defense.XBowMode.XBowMode
import org.danielholmes.coc.baseanalyser.model.range.CircularElementRange
import org.danielholmes.coc.baseanalyser.model.Target.Target
import org.danielholmes.coc.baseanalyser.model.{StationaryDefensiveBuilding, PreventsTroopDrop, Target, Tile}
import org.scalactic.anyvals.PosInt

case class XBow(level: PosInt, tile: Tile, private val mode: XBowMode) extends StationaryDefensiveBuilding with PreventsTroopDrop {
  val targets = XBowMode.targets(mode)
  val size = PosInt(3)
  lazy val range = CircularElementRange(block.centre, XBowMode.radiusSize(mode))
}

object XBow {
  def ground(level: PosInt, tile: Tile): XBow = {
    XBow(level, tile, XBowMode.Ground)
  }

  def both(level: PosInt, tile: Tile): XBow = {
    XBow(level, tile, XBowMode.Both)
  }
}

object XBowMode extends Enumeration {
  type XBowMode = Value

  val Ground, Both = Value

  def targets(mode: XBowMode): Set[Target] = {
    if (mode == Ground) {
      Target.GroundOnly
    } else {
      Target.Both
    }
  }

  def radiusSize(mode: XBowMode): PosInt = {
    if (mode == Ground) {
      14
    } else {
      11
    }
  }
}
