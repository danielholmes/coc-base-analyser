package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._
import org.scalatest._

class StringDisplayerSpec extends FlatSpec with Matchers {
  val DrawnRowSize = Tile.MaxCoordinate + 1 + 2 + 1 // All tiles, borders and a new line char
  val displayer = new StringDisplayer

  "String Displayer" should "display empty base" in {
    displayer.build(Village.empty) should be (EMPTY)
  }

  it should "display origin town hall" in {
    val result = displayer.build(Village(Set(TownHall(1, Tile(1, 1)))))
    for (row <- 2 to 5) {
      for (col <- 2 to 5) {
        result.charAt(row * DrawnRowSize.toInt + col) should be('#')
      }
    }
  }

  it should "display end of earth archer tower" in {
    val result = displayer.build(Village(Set(ArcherTower(1, Tile(41, 41)))))
    for (row <- 42 to 44) {
      for (col <- 42 to 44) {
        result.charAt(row * DrawnRowSize.toInt + col) should be('A')
      }
    }
  }

  it should "display end of map cc radius without exception" in {
    displayer.build(Village(Set(ClanCastle(1, Tile(41, 41)))))
    //result.charAt(1600) should be('^')
  }

  val EMPTY =
"""+----------------------------------------------+
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
|                                              |
+----------------------------------------------+
"""
}