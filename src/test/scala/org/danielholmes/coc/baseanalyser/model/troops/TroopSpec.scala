package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.trash.{ArmyCamp, BuilderHut}
import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.{PosDouble, PosZDouble}
import org.scalatest._

class TroopSpec extends FlatSpec with Matchers {
  "Troop" should "return correct points can attack building from" in {
    val LowCornerCoord = PosDouble.from(2.0 - Math.sqrt(0.75 / 2)).get
    val HighCornerCoord = PosDouble.from(6.0 - LowCornerCoord).get
    ExampleTroop.getAttackPositions(BuilderHut(Tile(2, 2))) should contain theSameElementsAs
      (Set(
        MapCoordinate(LowCornerCoord, LowCornerCoord), MapCoordinate(2, 1.25), MapCoordinate(3, 1.25),
          MapCoordinate(4, 1.25), MapCoordinate(HighCornerCoord, LowCornerCoord),
          MapCoordinate(1.25, 2), MapCoordinate(2, 2), MapCoordinate(3, 2), MapCoordinate(4, 2), MapCoordinate(4.75, 2),
          MapCoordinate(1.25, 3), MapCoordinate(2, 3), MapCoordinate(3, 3), MapCoordinate(4, 3), MapCoordinate(4.75, 3),
          MapCoordinate(1.25, 4), MapCoordinate(2, 4), MapCoordinate(3, 4), MapCoordinate(4, 4), MapCoordinate(4.75, 4),
          MapCoordinate(LowCornerCoord, HighCornerCoord), MapCoordinate(2, 4.75), MapCoordinate(3, 4.75),
          MapCoordinate(4, 4.75), MapCoordinate(HighCornerCoord, HighCornerCoord)
      ))
  }

  it should "find targets with correct hit area" in {
    val armyCamp = ArmyCamp(1, Tile(10, 10))
    ExampleTroop.findTargets(TileCoordinate(10, 10), Village(Set(armyCamp))) should contain theSameElementsAs Set(armyCamp)
  }
}

object ExampleTroop extends Troop {
  val Range = PosZDouble(0.75)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getAnyBuildingsTargets(village)
  }
}