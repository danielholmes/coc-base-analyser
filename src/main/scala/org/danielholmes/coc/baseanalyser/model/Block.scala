package org.danielholmes.coc.baseanalyser.model

case class Block(private val coordinate: TileCoordinate, private val tileSize: TileSize) {
  require(coordinate != null, "coordinate musnt be null")
  require(tileSize != null, "tileSize musnt be null")

  val x = coordinate.x
  val y = coordinate.y
  val size = tileSize.toInt
  val oppositeX = x + size.toInt
  val oppositeY = y + size.toInt

  def findClosestCoordinate(from: TileCoordinate): TileCoordinate = {
    possibleIntersectionPoints.min(Ordering.by((_: TileCoordinate)
      .distanceTo(from)))
  }

  private def possibleIntersectionPoints: Set[TileCoordinate] = {
    coordinate.yAxisCoordsTo(coordinate.offsetY(size))
      .map(_.xAxisCoordsTo(coordinate.offsetX(size)))
      .flatten
      .toSet
  }
}
