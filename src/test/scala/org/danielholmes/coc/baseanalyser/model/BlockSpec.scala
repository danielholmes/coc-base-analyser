package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class BlockSpec extends FlatSpec with Matchers {
  val block = Block(TileCoordinate(5, 5), TileSize(3))

  "Block" should "find closest coordinate in x axis left" in {
    block.findClosestCoordinate(TileCoordinate(0, 5)) should be (TileCoordinate(5, 5))
  }

  it should "find closest coordinate in x axis right" in {
    block.findClosestCoordinate(TileCoordinate(10, 5)) should be (TileCoordinate(8, 5))
  }

  it should "find closest coordinate in y axis up" in {
    block.findClosestCoordinate(TileCoordinate(5, 0)) should be (TileCoordinate(5, 5))
  }

  it should "find closest coordinate in y axis down" in {
    block.findClosestCoordinate(TileCoordinate(5, 10)) should be (TileCoordinate(5, 8))
  }

  it should "find closest coordinate diagonally" in {
    block.findClosestCoordinate(TileCoordinate(10, 10)) should be (TileCoordinate(8, 8))
  }
}