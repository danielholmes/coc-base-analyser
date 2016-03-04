package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class DragonSpec extends FlatSpec with Matchers {
  val origin = TileCoordinate(0, 0)

  "Dragon" should "return correct points can attack building from" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, TileCoordinate(1, 1))) should contain theSameElementsAs
      TileCoordinate.Origin.matrixOfCoordinatesTo(TileCoordinate(4, 4))
  }

  it should "return correct points can attack building from when at origin" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, TileCoordinate.Origin)) should contain theSameElementsAs
      TileCoordinate.Origin.matrixOfCoordinatesTo(TileCoordinate(3, 3))
  }

  it should "return correct points can attack building from when at end" in {
    Dragon.getCoordinatesCanAttackElementFrom(BuilderHut(1, TileCoordinate.End.offset(-2, -2))) should contain theSameElementsAs
      TileCoordinate(41, 41).matrixOfCoordinatesTo(TileCoordinate.End)
  }
}