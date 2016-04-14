package org.danielholmes.coc.baseanalyser.model

import org.scalatest._
import org.scalactic.anyvals.{PosZInt, PosZDouble}

class TileSpec extends FlatSpec with Matchers {
  "Tile" should "return correct matrix of tiles" in {
    Tile(2, 2).matrixOfTilesInDirection(2, 2) should contain theSameElementsAs
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
    Tile(Tile.MaxCoordinate, Tile.MaxCoordinate).touchingTiles should be (Set(
      Tile(PosZInt.from(Tile.MaxCoordinate - 1).get, PosZInt.from(Tile.MaxCoordinate - 1).get),
      Tile(PosZInt.from(Tile.MaxCoordinate - 1).get, Tile.MaxCoordinate),
      Tile(Tile.MaxCoordinate, PosZInt.from(Tile.MaxCoordinate - 1).get)
    ))
  }

  it should "return correct distance to touching" in {
    Tile(1, 0).distanceTo(Tile(2, 0)) should be (PosZDouble(0))
  }

  it should "return correct distance to x difference" in {
    Tile(0, 0).distanceTo(Tile(11, 0)) should be (PosZDouble(10))
  }

  it should "return correct distance to diagonal" in {
    Tile(10, 10).distanceTo(Tile(6, 5)) should be (PosZDouble(5))
  }

  it should "return correct shortest tile path to straight" in {
    Tile(11, 10).shortestTilePathTo(Tile(14, 10), Set.empty) should contain (List(
      Tile(11, 10), Tile(12, 10), Tile(13, 10), Tile(14, 10)
    ))
  }

  it should "return correct shortest tile path to diagonal" in {
    Tile(10, 10).shortestTilePathTo(Tile(13, 13), Set.empty) should contain (List(
      Tile(10, 10), Tile(11, 11), Tile(12, 12), Tile(13, 13)
    ))
  }

  it should "return correct shortest tile path with wall in the way" in {
    val wallTiles = Tile(11, 9).matrixOfTilesTo(Tile(11, 20))
    Tile(10, 10).shortestTilePathTo(Tile(12, 10), wallTiles) should contain (List(
      Tile(10, 10), Tile(10, 9), Tile(11, 8), Tile(12, 9), Tile(12, 10)
    ))
  }
}