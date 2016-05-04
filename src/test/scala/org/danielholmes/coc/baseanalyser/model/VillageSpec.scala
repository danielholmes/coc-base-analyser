package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.model.trash.{Barrack, BuilderHut}
import org.danielholmes.coc.baseanalyser.util.ElementsBuilder
import org.scalatest._

class VillageSpec extends FlatSpec with Matchers {
  "Village" should "return every coordinate when empty" in {
    Village.empty.coordinatesAllowedToDropTroop should contain theSameElementsAs TileCoordinate.All
  }

  it should "return all with hit areas excluded" in {
    val builderPlacement = TileCoordinate(2, 2).matrixOfCoordinatesTo(TileCoordinate(4, 4))
    val expected = TileCoordinate.All.toSet -- builderPlacement

    val village = Village(Set(BuilderHut(Tile(2, 2))))
    val result = village.coordinatesAllowedToDropTroop
    result should have size expected.size
    result should contain theSameElementsAs expected
  }

  it should "return all with hit areas excluded when on edge of map" in {
    val builderPlacement = TileCoordinate.MapOrigin.matrixOfCoordinatesTo(TileCoordinate.MapOrigin.offset(2, 2))
    val expected = TileCoordinate.All.toSet -- builderPlacement

    val village = Village(Set(BuilderHut(Tile.MapOrigin)))
    val result = village.coordinatesAllowedToDropTroop
    result should have size expected.size
    result should contain theSameElementsAs expected
  }

  it should "disallow overlapping elements" in {
    a[IllegalArgumentException] should be thrownBy {
      Village(
        Set(
          Barrack(1, Tile.MapOrigin),
          Barrack(1, Tile.MapOrigin.offset(1, 1))
        )
      )
    }
  }

  it should "not include intersections of attack placements in coordinates allowed to drop troop" in {
    val village = Village(Set(BuilderHut(Tile(1, 1)), BuilderHut(Tile(5, 1))))

    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 1)
    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 2)
    village.coordinatesAllowedToDropTroop should not contain TileCoordinate(4, 3)
  }

  it should "return no compartments for empty village" in {
    Village.empty.wallCompartments should be (empty)
  }

  it should "return no compartments for village with walls but no compartments" in {
    ElementsBuilder.villageFromString("WWW\nW W\n WW", Tile(1, 1), Wall(1, _)).wallCompartments should be (empty)
  }

  it should "return single simple compartment" in {
    val walls = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(5, 5), Wall(1, _))
    Village(walls.map(_.asInstanceOf[Element])).wallCompartments should be (Set(WallCompartment(
      walls, Set(Tile(6, 6)), Set.empty
    )))
  }

  it should "return single compartment with a building inside" in {
    val walls = ElementsBuilder.elementFence(Tile(6, 6), 5, 5)
    val barrack = Barrack(1, Tile(7, 7))

    Village(walls + barrack).wallCompartments should be (Set(WallCompartment(
      walls.map(_.asInstanceOf[Wall]),
      Tile(7, 7).matrixOfTilesTo(Tile(9, 9)),
      Set(barrack)
    )))
  }

  it should "return multiple compartments" in {
    val walls1 = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(6, 6), Wall(1, _))
    val walls2 = ElementsBuilder.fromString("WWW\nW W\nWWW", Tile(16, 6), Wall(1, _))
    Village((walls1 ++ walls2).map(_.asInstanceOf[Element])).wallCompartments should be (Set(
      WallCompartment(walls1, Set(Tile(7, 7)), Set.empty),
      WallCompartment(walls2, Set(Tile(17, 7)), Set.empty)
    ))
  }
}
