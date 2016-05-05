package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.trash.{ArmyCamp, BuilderHut}
import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.{PosDouble, PosZDouble}
import org.scalatest._

class TroopSpec extends FlatSpec with Matchers {
  "Troop" should "return correct points can attack building from" in {
    val LowCornerCoord = PosDouble.from(2.0 - Math.sqrt(0.75 / 2)).get
    val HighCornerCoord = PosDouble.from(6.0 - LowCornerCoord).get
    ExampleTroop.getAttackFloatCoordinates(BuilderHut(Tile(2, 2))) should contain theSameElementsAs
      (Set(
        FloatMapCoordinate(LowCornerCoord, LowCornerCoord), FloatMapCoordinate(2, 1.25), FloatMapCoordinate(3, 1.25),
          FloatMapCoordinate(4, 1.25), FloatMapCoordinate(HighCornerCoord, LowCornerCoord),
          FloatMapCoordinate(1.25, 2), FloatMapCoordinate(2, 2), FloatMapCoordinate(3, 2), FloatMapCoordinate(4, 2), FloatMapCoordinate(4.75, 2),
          FloatMapCoordinate(1.25, 3), FloatMapCoordinate(2, 3), FloatMapCoordinate(3, 3), FloatMapCoordinate(4, 3), FloatMapCoordinate(4.75, 3),
          FloatMapCoordinate(1.25, 4), FloatMapCoordinate(2, 4), FloatMapCoordinate(3, 4), FloatMapCoordinate(4, 4), FloatMapCoordinate(4.75, 4),
          FloatMapCoordinate(LowCornerCoord, HighCornerCoord), FloatMapCoordinate(2, 4.75), FloatMapCoordinate(3, 4.75),
          FloatMapCoordinate(4, 4.75), FloatMapCoordinate(HighCornerCoord, HighCornerCoord)
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