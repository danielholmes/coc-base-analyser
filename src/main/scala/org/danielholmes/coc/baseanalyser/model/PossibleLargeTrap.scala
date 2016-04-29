package org.danielholmes.coc.baseanalyser.model

case class PossibleLargeTrap(tile: Tile)

object PossibleLargeTrap {
  implicit def widenToBlock(trap: PossibleLargeTrap): Block = Block(trap.tile, 2)
}
