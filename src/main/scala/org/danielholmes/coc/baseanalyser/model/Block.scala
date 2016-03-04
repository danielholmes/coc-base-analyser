package org.danielholmes.coc.baseanalyser.model

case class Block(private val coordinate: TileCoordinate, private val width: TileSize, private val height: TileSize) {
  require(coordinate != null, "coordinate musn't be null")
  require(width != null, "width musn't be null")
  require(height != null, "width musn't be null")
  require(coordinate.x + width.toInt <= TileCoordinate.Max, s"x coord ${coordinate.x} + ${width.toInt} must be within map")
  require(coordinate.y + height.toInt <= TileCoordinate.Max, s"y coord ${coordinate.y} + ${height.toInt} must be within map")

  val x = coordinate.x
  val y = coordinate.y
  private val oppositeCoordinate = coordinate.offset(width.toInt, height.toInt)
  val oppositeX = oppositeCoordinate.x
  val oppositeY = oppositeCoordinate.y

  val centre = MapCoordinate(x + width.toDouble / 2.0, y + height.toDouble / 2.0)

  def internalCoordinates: Set[TileCoordinate] = {
    if (width < 2 || height < 2) return Set.empty
    coordinate.offset(1, 1)
      .matrixOfCoordinatesTo(oppositeCoordinate.offset(-1, -1))
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

  def createWithin(coordinate: TileCoordinate, coordOffsetX: Int, coordOffsetY: Int, size: TileSize) = {
    val actualX = Math.max(coordinate.x + coordOffsetX, 0)
    val actualY = Math.max(coordinate.y + coordOffsetY, 0)
    val actualRight = Math.min(coordinate.x + coordOffsetX + size.toInt, TileCoordinate.Max)
    val actualBottom = Math.min(coordinate.y + coordOffsetY + size.toInt, TileCoordinate.Max)
    Block(
      TileCoordinate(actualX, actualY),
      TileSize(actualRight - actualX),
      TileSize(actualBottom - actualY)
    )
  }

  private def possibleIntersectionPoints: Set[TileCoordinate] = {
    coordinate.matrixOfCoordinatesTo(coordinate.offset(width, height))
  }
}

object Block {
  val Map = Block(TileCoordinate.Origin, TileSize(TileCoordinate.Max))

  def apply(coordinate: TileCoordinate, size: TileSize): Block = apply(coordinate, size, size)

  def anyIntersect(blocks: Set[Block]): Boolean = {
    blocks.exists(anyIntersect(_, blocks.toSeq))
  }

  private def anyIntersect(block: Block, blocks: Seq[Block]): Boolean = {
    if (blocks.isEmpty) return false
    if (block == blocks.head) return anyIntersect(block, blocks.tail)
    block.intersects(blocks.head) || anyIntersect(block, blocks.tail)
  }
}
