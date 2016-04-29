package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class WallCompartmentSpec extends FlatSpec with Matchers {
  "WallCompartment" should "return empty tiles for no buildings" in {
    val walls = ElementsBuilder.wallFence(Tile.MapOrigin, 3, 3)
    val inner = Set(Tile.MapOrigin.offset(1, 1))
    WallCompartment(walls, inner, Set.empty).emptyTiles should contain theSameElementsAs inner
  }

  it should "return empty tiles for building inside" in {
    val walls = ElementsBuilder.wallFence(Tile.MapOrigin, 6, 5)
    val inner = Tile.MapOrigin.offset(1, 1).matrixOfTilesTo(Tile.MapOrigin.offset(4, 3))
    val buildings = Set[Element](Barrack(1, Tile.MapOrigin.offset(2, 1)))
    WallCompartment(walls, inner, buildings).emptyTiles should contain theSameElementsAs
      Tile.MapOrigin.offset(1, 1).matrixOfTilesTo(Tile.MapOrigin.offset(1, 3))
  }

  it should "return empty possible large traps for 1x1 only" in {
    WallCompartment(ElementsBuilder.wallFence(Tile.MapOrigin, 3, 3), Set(Tile.MapOrigin.offset(1, 1)), Set.empty).possibleLargeTraps should be (empty)
  }

  it should "return 2 possible large traps for 3x2 empty space" in {
    val walls = ElementsBuilder.wallFence(Tile.MapOrigin, 5, 4)
    val inner = Tile.MapOrigin.offset(1, 1).matrixOfTilesTo(Tile.MapOrigin.offset(3, 2))
    WallCompartment(walls, inner, Set.empty).possibleLargeTraps should contain theSameElementsAs
      Set(Block(Tile.MapOrigin.offset(1, 1), 2), Block(Tile.MapOrigin.offset(2, 1), 2))
  }
}
