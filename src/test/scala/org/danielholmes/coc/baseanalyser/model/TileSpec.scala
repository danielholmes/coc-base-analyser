package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class TileSpec extends FlatSpec with Matchers {
  "Tile" should "return correct matrix of tiles" in {
    Tile(2, 2).matrixOfTilesInDirection(TileSize(2), TileSize(2)) should contain theSameElementsAs
      Set(Tile(2, 2), Tile(2, 3), Tile(3, 2), Tile(3, 3))
  }
}