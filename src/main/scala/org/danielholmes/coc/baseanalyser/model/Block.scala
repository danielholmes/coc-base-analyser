package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.{PosInt, PosZDouble, PosZInt}

case class Block(tile: Tile, size: PosInt) {
  require(tile != null, "coordinate musn't be null")
  require(tile.x + size <= TileCoordinate.MaxCoordinate, s"x coord ${tile.x} + $size must be within coordinate system")
  require(tile.y + size <= TileCoordinate.MaxCoordinate, s"y coord ${tile.y} + $size must be within coordinate system")

  val x = tile.x
  val y = tile.y
  private lazy val oppositeCoordinate = tile.toMapCoordinate.offset(size, size)
  lazy val oppositeX = oppositeCoordinate.x
  lazy val oppositeY = oppositeCoordinate.y

  lazy val isWithinMap = tiles.forall(_.isWithinMap)

  lazy val centre = MapCoordinate(PosZDouble.from(x + size.toDouble / 2.0).get, PosZDouble.from(y + size.toDouble / 2.0).get)

  lazy val internalCoordinates: Set[TileCoordinate] = {
    if (size < 2) {
      Set.empty
    } else {
      tile.toMapCoordinate
        .offset(1, 1)
        .matrixOfCoordinatesTo(oppositeCoordinate.offset(-1, -1))
    }
  }

  lazy val allCoordinates: Set[TileCoordinate] = {
    tile.toMapCoordinate.matrixOfCoordinatesTo(oppositeCoordinate)
  }

  lazy val tiles: Set[Tile] = {
    tile.matrixOfTilesInDirection(size, size)
  }

  def findClosestCoordinate(from: TileCoordinate): TileCoordinate = {
    possibleIntersectionPoints.min(Ordering.by((_: TileCoordinate)
      .distanceTo(from)))
  }

  def distanceTo(from: TileCoordinate) = {
    findClosestCoordinate(from).distanceTo(from)
  }

  def intersects(other: Block) = {
    x < other.oppositeX && oppositeX > other.x &&
      y < other.oppositeY && oppositeY > other.y
  }

  def expandBy(offset: PosInt): Block = expandToSize(PosInt.from(size + (offset * 2)).get)

  def expandToSize(newSize: PosInt): Block = {
    if (newSize < size) throw new IllegalArgumentException("newSize must be greater than size")
    if ((size - newSize) % 2 != 0) throw new IllegalArgumentException("Must increase by factors of 2")
    if (newSize == size) return this
    val sizeDiff = newSize - size
    Block(
      Tile(PosZInt.from(tile.x - sizeDiff / 2).get, PosZInt.from(tile.y - sizeDiff / 2).get),
      newSize
    )
  }

  private lazy val possibleIntersectionPoints: Set[TileCoordinate] = {
    tile.toMapCoordinate.matrixOfCoordinatesTo(tile.toMapCoordinate.offset(size, size))
  }
}

object Block {
  val Map = Block(Tile.Origin, TileCoordinate.MaxCoordinate)

  def firstIntersecting(blocks: Set[Block]): Option[(Block, Block)] = {
    blocks.map(b => firstIntersecting(b, blocks.toSeq).map((_, b)))
      .headOption
      .getOrElse(None)
  }

  private def firstIntersecting(block: Block, blocks: Seq[Block]): Option[Block] = {
    if (blocks.isEmpty) return None
    if (block == blocks.head) return firstIntersecting(block, blocks.tail)
    if (block.intersects(blocks.head)) return Some(blocks.head)
    firstIntersecting(block, blocks.tail)
  }
}
