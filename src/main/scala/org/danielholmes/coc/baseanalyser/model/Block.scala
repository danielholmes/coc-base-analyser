package org.danielholmes.coc.baseanalyser.model

case class Block(tile: Tile, size: TileSize) {
  require(tile != null, "coordinate musn't be null")
  require(tile.x + size.toInt <= TileCoordinate.Max.toInt, s"x coord ${tile.x} + ${size.toInt} must be within coordinate system")
  require(tile.y + size.toInt <= TileCoordinate.Max.toInt, s"y coord ${tile.y} + ${size.toInt} must be within coordinate system")

  val x = tile.x
  val y = tile.y
  private lazy val oppositeCoordinate = tile.toMapCoordinate.offset(size.toInt, size.toInt)
  lazy val oppositeX = oppositeCoordinate.x
  lazy val oppositeY = oppositeCoordinate.y

  lazy val isWithinMap = tiles.forall(_.isWithinMap)

  lazy val centre = MapCoordinate(x + size.toDouble / 2.0, y + size.toDouble / 2.0)

  lazy val internalCoordinates: Set[TileCoordinate] = {
    if (size < TileSize(2)) {
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

  def distanceFrom(from: TileCoordinate) = {
    findClosestCoordinate(from).distanceTo(from)
  }

  def intersects(other: Block) = {
    x < other.oppositeX && oppositeX > other.x &&
      y < other.oppositeY && oppositeY > other.y
  }

  def expandBy(offset: TileSize): Block = {
    expandToSize(size + (offset * 2))
  }

  def expandToSize(newSize: TileSize): Block = {
    if (newSize < size) throw new IllegalArgumentException("newSize must be greater than size")
    if ((size - newSize) % 2 != 0) throw new IllegalArgumentException("Must increase by factors of 2")
    if (newSize == size) return this
    val sizeDiff = newSize - size
    Block(
      Tile(tile.x - sizeDiff.toInt / 2, tile.y - sizeDiff.toInt / 2),
      newSize
    )
  }

  private lazy val possibleIntersectionPoints: Set[TileCoordinate] = {
    tile.toMapCoordinate.matrixOfCoordinatesTo(tile.toMapCoordinate.offset(size.toInt, size.toInt))
  }
}

object Block {
  val Map = Block(Tile.Origin, TileSize(TileCoordinate.Max.toInt))

  def anyIntersect(blocks: Set[Block]): Boolean = {
    blocks.exists(anyIntersect(_, blocks.toSeq))
  }

  def getAnyIntersection(blocks: Set[Block]): Option[Block] = {
    blocks.find(anyIntersect(_, blocks.toSeq))
  }

  private def anyIntersect(block: Block, blocks: Seq[Block]): Boolean = {
    if (blocks.isEmpty) return false
    if (block == blocks.head) return anyIntersect(block, blocks.tail)
    block.intersects(blocks.head) || anyIntersect(block, blocks.tail)
  }
}
