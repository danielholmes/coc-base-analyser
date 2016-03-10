package org.danielholmes.coc.baseanalyser.model

trait Trap extends Element {
  override lazy val preventTroopDropSize: TileSize = TileSize(0)
}
