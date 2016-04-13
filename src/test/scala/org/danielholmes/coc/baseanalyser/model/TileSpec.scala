package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class TileSpec extends FlatSpec with Matchers {
  "Tile" should "return correct matrix of tiles" in {
    Tile(2, 2).matrixOfTilesInDirection(TileSize(2), TileSize(2)) should contain theSameElementsAs
      Set(Tile(2, 2), Tile(2, 3), Tile(3, 2), Tile(3, 3))
  }

  it should "return correct touching tiles" in {
    Tile(1, 1).touchingTiles should be (Set(
      Tile(0, 0), Tile(1, 0), Tile(2, 0),
      Tile(0, 1), /*Tile(1, 1),*/ Tile(2, 1),
      Tile(0, 2), Tile(1, 2), Tile(2, 2)
    ))
  }

  it should "return correct touching tiles at origin" in {
    Tile(0, 0).touchingTiles should be (Set(
      /*Tile(0, 0), */ Tile(1, 0),
      Tile(0, 1), Tile(1, 1)
    ))
  }

  it should "return correct touching tiles at end" in {
    Tile(Tile.Max, Tile.Max).touchingTiles should be (Set(
      Tile(Tile.Max - 1, Tile.Max - 1),
      Tile(Tile.Max - 1, Tile.Max),
      Tile(Tile.Max, Tile.Max - 1)
    ))
  }
}