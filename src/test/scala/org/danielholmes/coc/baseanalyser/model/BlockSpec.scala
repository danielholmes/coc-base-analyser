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

  it should "return empty internal coords when 1x1" in {
    Block(TileCoordinate(0, 0), TileSize(1)).internalCoordinates should be (Set.empty)
  }

  it should "return empty internal coords when 2x2" in {
    Block(TileCoordinate(0, 0), TileSize(2)).internalCoordinates should contain theSameElementsAs Set(TileCoordinate(1, 1))
  }

  it should "return correct internal coords when 3x3 or more" in {
    Block(TileCoordinate(0, 0), TileSize(3)).internalCoordinates should contain theSameElementsAs
      Set(TileCoordinate(1, 1), TileCoordinate(2, 1), TileCoordinate(1, 2), TileCoordinate(2, 2))
  }

  it should "return true intersect for overlapping items" in {
    Block(TileCoordinate.Origin, TileSize(2)).intersects(Block(TileCoordinate(1, 1), TileSize(2))) should be (true)
  }

  it should "return true any intersect for no items" in {
    Block.anyIntersect(Set.empty) should be (false)
  }

  it should "return false any intersect for no items" in {
    Block.anyIntersect(Set(Block(TileCoordinate.Origin, TileSize(2)))) should be (false)
  }

  it should "return false any intersect for one item" in {
    Block.anyIntersect(Set(Block(TileCoordinate.Origin, TileSize(2)))) should be (false)
  }

  it should "return false any intersect for edge touching items" in {
    Block.anyIntersect(Set(Block(TileCoordinate.Origin, TileSize(2)), Block(TileCoordinate(0, 2), TileSize(2)))) should be (false)
  }

  it should "return true any intersect for overlapping items" in {
    Block.anyIntersect(Set(Block(TileCoordinate.Origin, TileSize(2)), Block(TileCoordinate(1, 1), TileSize(2)))) should be (true)
  }
}