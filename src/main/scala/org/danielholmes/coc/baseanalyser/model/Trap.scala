package org.danielholmes.coc.baseanalyser.model

trait Trap extends Element {
  override val attackPlacementSize: TileSize = TileSize(0)
}
