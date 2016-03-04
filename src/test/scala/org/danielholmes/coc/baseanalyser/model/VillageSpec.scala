package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class VillageSpec extends FlatSpec with Matchers {
  "Village" should "return every coordinate when empty" in {
    Village.empty.attackPlacementCoordinates should be (TileCoordinate.All.toSet)
  }

  it should "return all with hit areas excluded" in {
    val builderPlacement = Set(
      TileCoordinate(1, 1),
      TileCoordinate(2, 1),
      TileCoordinate(3, 1),
      TileCoordinate(1, 2),
      TileCoordinate(2, 2),
      TileCoordinate(3, 2),
      TileCoordinate(1, 3),
      TileCoordinate(2, 3),
      TileCoordinate(3, 3)
    )
    val expected = TileCoordinate.All.toSet -- builderPlacement

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