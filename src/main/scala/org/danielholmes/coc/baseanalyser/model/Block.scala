package org.danielholmes.coc.baseanalyser.model

case class Block(private val tile: Tile, val width: TileSize, val height: TileSize) {
  require(tile != null, "coordinate musn't be null")
  require(width != null, "width musn't be null")
  require(height != null, "width musn't be null")
  require(tile.x + width.toInt <= MapTileCoordinate.Max, s"x coord ${tile.x} + ${width.toInt} must be within map")
  require(tile.y + height.toInt <= MapTileCoordinate.Max, s"y coord ${tile.y} + ${height.toInt} must be within map")

  val x = tile.x
  val y = tile.y
  private lazy val oppositeCoordinate = tile.toMapCoordinate.offset(width.toInt, height.toInt)
  lazy val oppositeX = oppositeCoordinate.x
  lazy val oppositeY = oppositeCoordinate.y

  lazy val centre = MapCoordinate(x + width.toDouble / 2.0, y + height.toDouble / 2.0)

  lazy val internalCoordinates: Set[MapTileCoordinate] = {
    if (width < 2 || height < 2) {
      Set.empty
    } else {
      tile.toMapCoordinate
        .offset(1, 1)
        .matrixOfCoordinatesTo(oppositeCoordinate.offset(-1, -1))
    }
  }

  lazy val coordinates: Set[MapTileCoordinate] = {
    tile.toMapCoordinate.matrixOfCoordinatesTo(oppositeCoordinate)
  }

  lazy val tiles: Set[Tile] = {
    tile.matrixOfTilesInDirection(width, height)
  }

  def findClosestCoordinate(from: MapTileCoordinate): MapTileCoordinate = {
    possibleIntersectionPoints.min(Ordering.by((_: MapTileCoordinate)
      .distanceTo(from)))
  }

  def distanceFrom(from: MapTileCoordinate) = {
    findClosestCoordinate(from).distanceTo(from)
  }

  def intersects(other: Block) = {
    x < other.oppositeX && oppositeX > other.x &&
      y < other.oppositeY && oppositeY > other.y
  }

  def expandWithinMap(size: TileSize): Block = {
    Block.Map.createWithin(tile, -size.toInt, -size.toInt, width + size * 2, height + size * 2)
  }

  def createWithin(tile: Tile, coordOffsetX: Int, coordOffsetY: Int, size: TileSize): Block = {
    createWithin(tile, coordOffsetX, coordOffsetY, size, size)
  }

  def createWithin(tile: Tile, coordOffsetX: Int, coordOffsetY: Int, width: TileSize, height: TileSize): Block = {
    val actualX = Math.max(tile.x + coordOffsetX, 0)
    val actualY = Math.max(tile.y + coordOffsetY, 0)
    val actualRight = Math.min(tile.x + coordOffsetX + width.toInt, MapTileCoordinate.Max)
    val actualBottom = Math.min(tile.y + coordOffsetY + height.toInt, MapTileCoordinate.Max)
    Block(
      Tile(actualX, actualY),
      TileSize(actualRight - actualX),
      TileSize(actualBottom - actualY)
    )
  }

  private lazy val possibleIntersectionPoints: Set[MapTileCoordinate] = {
    tile.toMapCoordinate.matrixOfCoordinatesTo(tile.toMapCoordinate.offset(width, height))
  }
}

object Block {
  val Map = Block(Tile.Origin, TileSize(MapTileCoordinate.Max))

  def apply(tile: Tile, size: TileSize): Block = apply(tile, size, size)

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
