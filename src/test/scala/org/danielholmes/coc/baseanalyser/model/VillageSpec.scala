package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class VillageSpec extends FlatSpec with Matchers {
  "Village" should "return every coordinate when empty" in {
    Village.empty.coordinatesAllowedToDropTroop should contain theSameElementsAs TileCoordinate.All
  }

  it should "return all with hit areas excluded" in {
    val builderPlacement = TileCoordinate(2, 2).matrixOfCoordinatesTo(TileCoordinate(4, 4))
    val expected = TileCoordinate.All.toSet -- builderPlacement

    val village = Village(Set(BuilderHut(1, Tile(2, 2))))
    val result = village.coordinatesAllowedToDropTroop
    result should have size expected.size
    result should contain theSameElementsAs expected
  }

  it should "return all with hit areas excluded when on edge of map" in {
    val builderPlacement = TileCoordinate.MapOrigin.matrixOfCoordinatesTo(TileCoordinate.MapOrigin.offset(2, 2))
    val expected = TileCoordinate.All.toSet -- builderPlacement

    val village = Village(Set(BuilderHut(1, Tile.MapOrigin)))
    val result = village.coordinatesAllowedToDropTroop
    result should have size expected.size
    result should contain theSameElementsAs expected
  }

  it should "disallow overlapping elements" in {
    a [IllegalArgumentException] should be thrownBy {
      Village(
        Set(
          Barrack(1, Tile.MapOrigin),
          Barrack(1, Tile.MapOrigin.offset(1, 1))
        )
      )
    }
  }

  it should "not include intersections of attack placements in coordinates allowed to drop troop" in {
    val village = Village(Set(BuilderHut(1, Tile(1, 1)), BuilderHut(1, Tile(5, 1))))

    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 1)
    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 2)
    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 3)
  }
}