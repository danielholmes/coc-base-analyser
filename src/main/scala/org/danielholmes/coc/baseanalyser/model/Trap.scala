package org.danielholmes.coc.baseanalyser.model

trait Trap extends Element {
  override lazy val attackPlacementSize: TileSize = TileSize(0)
}
