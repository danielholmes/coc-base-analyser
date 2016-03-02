package org.danielholmes.coc.baseanalyser.model

import Target._

case class XBow(level: Int, coordinate: TileCoordinate, targets: Set[Target], range: Range) extends Element(level, coordinate) with Defense {
  val size = TileSize(3)
}
