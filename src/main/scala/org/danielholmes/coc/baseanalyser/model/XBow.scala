package org.danielholmes.coc.baseanalyser.model

import Target._

case class XBow(level: Int, coordinate: TileCoordinate, targets: Set[Target], range: Range) extends Defense {
  val size = TileSize(3)
}
