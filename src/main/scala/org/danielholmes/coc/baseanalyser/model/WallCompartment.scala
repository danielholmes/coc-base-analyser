package org.danielholmes.coc.baseanalyser.model

import scala.annotation.tailrec

// TODO: Inner tiles should really be derivable from the walls (maybe not a good idea though if already calculated
// during construction
case class WallCompartment(walls: Set[Wall], innerTiles: Set[Tile], elements: Set[Element]) {
  //require(walls.nonEmpty) Currently use wall compartment to represent outer area in algorithms. Should change this
  require(innerTiles.nonEmpty)

  lazy val emptyTiles = innerTiles -- elements.map(_.block).flatMap(_.tiles)

  lazy val possibleLargeTraps = findPossibleLargeTraps(emptyTiles.toList, Set.empty)

  private lazy val allTiles = innerTiles ++ walls.map(_.block.tile)

  def contains(tile: Tile): Boolean = allTiles.contains(tile)

  @tailrec
  private def findPossibleLargeTraps(tiles: List[Tile], current: Set[Block]): Set[Block] = {
    tiles match {
      case Nil => current
      case head :: tail => findPossibleLargeTraps(
        tail,
        current ++ Some(Block(head, 2)).filter(b => b.tiles.subsetOf(emptyTiles))
      )
    }
  }
}
