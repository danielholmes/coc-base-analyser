package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class VillageSpec extends FlatSpec with Matchers {
  "Village" should "return every coordinate when empty" in {
    Village.empty.attackPlacementCoordinates should contain theSameElementsAs (TileCoordinate.AllElementPlacement.toSet)
  }

  it should "return all with hit areas excluded" in {
    val builderPlacement = TileCoordinate(1, 1).matrixOfCoordinatesTo(TileCoordinate(3, 3))
    val expected = TileCoordinate.AllElementPlacement.toSet -- builderPlacement

    val result = Village(Set(BuilderHut(1, TileCoordinate(1, 1)))).attackPlacementCoordinates
    result should have size expected.size
    result should contain theSameElementsAs expected
  }

  it should "disallow overlapping elements" in {
    a [IllegalArgumentException] should be thrownBy {
      Village(
        Set(
          Barrack(1, TileCoordinate.Origin),
          Barrack(1, TileCoordinate(1, 1))
        )
      )
    }
  }
}