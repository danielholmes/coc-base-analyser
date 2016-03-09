package org.danielholmes.coc.baseanalyser.model

import Target._

case class XBow(level: Int, tile: Tile, targets: Set[Target], radiusSize: TileSize) extends Defense {
  val size = TileSize(3)
  lazy val range = ElementRange(block.centre, radiusSize)
}

object XBow {
  def ground(level: Int, tile: Tile): XBow = {
    XBow(level, tile, Set(Target.Ground), TileSize(14))
  }

  def both(level: Int, tile: Tile): XBow = {
    XBow(level, tile, Target.Both, TileSize(11))
  }
}
