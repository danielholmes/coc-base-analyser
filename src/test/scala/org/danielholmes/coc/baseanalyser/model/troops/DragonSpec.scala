package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Tile, BuilderHut, MapTileCoordinate}
import org.scalatest._

class DragonSpec extends FlatSpec with Matchers {
  val origin = MapTileCoordinate(0, 0)

  "Dragon" should "return correct points can attack building from" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile(1, 1))) should contain theSameElementsAs
      MapTileCoordinate.Origin.matrixOfCoordinatesTo(MapTileCoordinate(4, 4))
  }

  it should "return correct points can attack building from when at origin" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile.Origin)) should contain theSameElementsAs
      MapTileCoordinate.Origin.matrixOfCoordinatesTo(MapTileCoordinate(3, 3))
  }

  it should "return correct points can attack building from when at end" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile.End.offset(-2, -2))) should contain theSameElementsAs
      MapTileCoordinate(41, 41).matrixOfCoordinatesTo(MapTileCoordinate.End)
  }
}