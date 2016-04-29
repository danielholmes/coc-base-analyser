package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.{PosInt, PosZDouble, PosZInt}

import scala.annotation.tailrec

case class Block(tile: Tile, size: PosInt) {
  require(tile.x + size <= TileCoordinate.MaxCoordinate, s"x coord ${tile.x} + $size must be within coordinate system")
  require(tile.y + size <= TileCoordinate.MaxCoordinate, s"y coord ${tile.y} + $size must be within coordinate system")

  val x = tile.x
  val y = tile.y
  private lazy val oppositeTile = tile.offset(size, size)
  lazy val oppositeX = oppositeTile.x
  lazy val oppositeY = oppositeTile.y

  lazy val isWithinMap = tiles.forall(_.isWithinMap)

  lazy val centre = MapCoordinate(PosZDouble.from(x + size.toDouble / 2.0).get, PosZDouble.from(y + size.toDouble / 2.0).get)

  lazy val internalCoordinates = {
    if (size < 2) {
      Set.empty
    } else {
      tile.offset(1, 1)
        .matrixOfCoordinatesTo(oppositeTile.offset(-1, -1))
    }
  }

  lazy val topLeft: TileCoordinate = tile

  lazy val topRight = topLeft.offset(size, 0)

  lazy val bottomLeft = topLeft.offset(0, size)

  lazy val bottomRight = bottomLeft.offset(size, 0)


  lazy val leftSide = topLeft.matrixOfCoordinatesTo(bottomLeft)

  lazy val rightSide = topRight.matrixOfCoordinatesTo(bottomRight)

  lazy val topSide = topLeft.matrixOfCoordinatesTo(topRight)

  lazy val bottomSide = bottomLeft.matrixOfCoordinatesTo(bottomRight)


  lazy val allCoordinates: Set[TileCoordinate] = tile.matrixOfCoordinatesTo(oppositeTile)

  lazy val tiles = tile.matrixOfTilesInDirection(size, size)

  def findClosestCoordinate(from: MapCoordinate): TileCoordinate = {
    possibleIntersectionPoints.min(Ordering.by((_: TileCoordinate)
      .distanceTo(from)))
  }

  def distanceTo(from: TileCoordinate): PosZDouble = {
    findClosestCoordinate(from).distanceTo(from)
  }

  def intersects(other: Block): Boolean = {
    x < other.oppositeX && oppositeX > other.x &&
      y < other.oppositeY && oppositeY > other.y
  }

  def expandBy(offset: PosInt): Block = expandToSize(PosInt.from(size + (offset * 2)).get)

  def expandToSize(newSize: PosInt): Block = {
    if (newSize < size) throw new IllegalArgumentException("newSize must be greater than size")
    if ((size - newSize) % 2 != 0) throw new IllegalArgumentException("Must increase by factors of 2")
    if (newSize == size) {
      this
    } else {
      val sizeDiff = newSize - size
      Block(
        Tile(PosZInt.from(tile.x - sizeDiff / 2).get, PosZInt.from(tile.y - sizeDiff / 2).get),
        newSize
      )
    }
  }

  private lazy val possibleIntersectionPoints: Set[TileCoordinate] = {
    tile.matrixOfCoordinatesTo(tile.offset(size, size))
  }
}

object Block {
  val Map = Block(Tile.Origin, TileCoordinate.MaxCoordinate)

  def firstIntersecting(blocks: Set[Block]): Option[(Block, Block)] = {
    blocks.map(b => firstIntersecting(b, blocks).map((_, b)))
      .headOption
      .getOrElse(None)
  }

  @tailrec
  private def firstIntersecting(block: Block, blocks: Set[Block]): Option[Block] = {
    blocks.toList match {
      case Nil => None
      case head :: tail =>
        if (block != head && block.intersects(head)) {
          Some(head)
        } else {
          firstIntersecting(block, blocks.tail)
        }
    }
  }
}
