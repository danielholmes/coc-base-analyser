package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class ArcherSpec extends FlatSpec with Matchers {
  val origin = TileCoordinate(0, 0)

  "Archer" should "find targets with correct hit area" in {
    val armyCamp = ArmyCamp(1, Tile(10, 10))
    Archer.findTargets(TileCoordinate(9, 9), Village(Set(armyCamp))) should contain theSameElementsAs(Set(
      ArcherTargeting(TileCoordinate(9, 9), armyCamp)
    ))
  }
}
