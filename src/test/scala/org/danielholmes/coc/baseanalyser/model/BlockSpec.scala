package org.danielholmes.coc.baseanalyser.model

import org.scalatest._

class BlockSpec extends FlatSpec with Matchers {
  val block = Block(Tile(5, 5), 3)

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
    Block(Tile(0, 0), 1).internalCoordinates should be (Set.empty)
  }

  it should "return empty internal coords when 2x2" in {
    Block(Tile(0, 0), 2).internalCoordinates should contain theSameElementsAs Set(TileCoordinate(1, 1))
  }

  it should "return correct internal coords when 3x3 or more" in {
    Block(Tile(0, 0), 3).internalCoordinates should contain theSameElementsAs
      Set(TileCoordinate(1, 1), TileCoordinate(2, 1), TileCoordinate(1, 2), TileCoordinate(2, 2))
  }

  it should "return true intersect for overlapping items" in {
    Block(Tile.Origin, 2).intersects(Block(Tile(1, 1), 2)) should be (true)
  }

  it should "return true any intersect for no items" in {
    Block.anyIntersect(Set.empty) should be (false)
  }

  it should "return false any intersect for no items" in {
    Block.anyIntersect(Set(Block(Tile.Origin, 2))) should be (false)
  }

  it should "return false any intersect for one item" in {
    Block.anyIntersect(Set(Block(Tile.Origin, 2))) should be (false)
  }

  it should "return false any intersect for edge touching items" in {
    Block.anyIntersect(Set(Block(Tile.Origin, 2), Block(Tile(0, 2), 2))) should be (false)
  }

  it should "return true any intersect for overlapping items" in {
    Block.anyIntersect(Set(Block(Tile.Origin, 2), Block(Tile(1, 1), 2))) should be (true)
  }

  it should "return correct all tiles" in {
    Block(Tile.Origin, 2).tiles should contain theSameElementsAs Set(Tile.Origin, Tile(0, 1), Tile(1, 0), Tile(1, 1))
  }

  it should "return correct all coordinates" in {
    Block(Tile.Origin, 2).allCoordinates should contain theSameElementsAs
      Set(
        TileCoordinate(0, 0), TileCoordinate(1, 0), TileCoordinate(2, 0),
        TileCoordinate(0, 1), TileCoordinate(1, 1), TileCoordinate(2, 1),
        TileCoordinate(0, 2), TileCoordinate(1, 2), TileCoordinate(2, 2)
      )
  }

  it should "expand to size correctly" in {
    Block(Tile(1, 1), 2).expandToSize(4) should be (Block(Tile(0, 0), 4))
  }
}