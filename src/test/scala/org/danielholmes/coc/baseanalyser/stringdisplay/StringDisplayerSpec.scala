package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model.{TownHall, TileCoordinate, ArcherTower, Village}
import org.scalatest._

class StringDisplayerSpec extends FlatSpec with Matchers {
  val DrawnRowSize = 47
  val displayer = new StringDisplayer()

  "String Displayer" should "display empty base" in {
    displayer.build(Village.empty) should be (EMPTY)
  }

  it should "display origin town hall" in {
    val result = displayer.build(Village(Set(TownHall(1, new TileCoordinate(0, 0)))))
    for (row <- 1 to 4) {
      for (col <- 1 to 4) {
        result.charAt(row * DrawnRowSize + col) should be('#')
      }
    }
  }

  it should "display end of earth archer tower" in {
    val result = displayer.build(Village(Set(ArcherTower(1, new TileCoordinate(41, 41)))))
    for (row <- 42 to 44) {
      for (col <- 42 to 44) {
        result.charAt(row * DrawnRowSize + col) should be('A')
      }
    }
  }

  val EMPTY =
"""+--------------------------------------------+
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
|                                            |
+--------------------------------------------+
"""
}