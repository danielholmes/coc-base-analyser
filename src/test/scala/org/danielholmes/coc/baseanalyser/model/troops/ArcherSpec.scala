package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class ArcherSpec extends FlatSpec with Matchers {
  val origin = TileCoordinate(0, 0)

  "Archer" should "find targets with correct hit area" in {
    val armyCamp = ArmyCamp(1, Tile(10, 10))
    val result = Archer.findTarget(TileCoordinate(9, 9), Village(Set(armyCamp)))

    result.isDefined should be (true)
    result.get.standingPosition should be (TileCoordinate(9, 9))
    result.get.targeting should be (armyCamp)
    result.get.hitPoint should be (TileCoordinate(11, 11))
  }
}