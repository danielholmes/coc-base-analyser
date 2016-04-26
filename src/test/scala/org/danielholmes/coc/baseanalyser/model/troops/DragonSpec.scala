package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Tile, BuilderHut, TileCoordinate}
import org.scalatest._

class DragonSpec extends FlatSpec with Matchers {
  val origin = TileCoordinate(0, 0)

  "Dragon" should "return correct points can attack building from" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile(2, 2))) should contain theSameElementsAs
      TileCoordinate.MapOrigin.matrixOfCoordinatesTo(TileCoordinate(5, 5))
  }

  it should "return correct points can attack building from when at origin" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile.MapOrigin)) should contain theSameElementsAs
      TileCoordinate.Origin.matrixOfCoordinatesTo(TileCoordinate(4, 4))
  }

  it should "return correct points can attack building from when at end" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, Tile.MapEnd.offset(-2, -2))) should contain theSameElementsAs
      Tile.MapEnd.offset(-3, -3).toTileCoordinate.matrixOfCoordinatesTo(Tile.MapEnd.offset(1, 1).toTileCoordinate)
  }
}