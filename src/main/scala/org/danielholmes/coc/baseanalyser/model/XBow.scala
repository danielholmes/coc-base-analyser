package org.danielholmes.coc.baseanalyser.model

import Target._

case class XBow(level: Int, coordinate: TileCoordinate, targets: Set[Target], radiusSize: TileSize) extends Defense {
  val size = TileSize(3)
  def radius = Radius(block.centre, radiusSize)
}

object XBow {
  def ground(level: Int, coordinate: TileCoordinate): XBow = {
    XBow(level, coordinate, Set(Target.Ground), TileSize(14))
  }

  def both(level: Int, coordinate: TileCoordinate): XBow = {
    XBow(level, coordinate, Target.Both, TileSize(11))
  }
}
