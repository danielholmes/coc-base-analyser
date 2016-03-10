package org.danielholmes.coc.baseanalyser.model

case class TileCoordinate(x: Int, y: Int) {
  import TileCoordinate._

  require((0 to Max.toInt).contains(x), s"TileCoordinates.x must be >= 0 <= $Max, given: $x")
  require((0 to Max.toInt).contains(y), s"TileCoordinates.y must be >= 0 <= $Max, given: $y")

  def distanceTo(other: TileCoordinate): Double = distanceTo(other.toMapCoordinate)

  def distanceTo(other: MapCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }

  def offset(xAmount: Int, yAmount: Int): TileCoordinate = TileCoordinate(x + xAmount, y + yAmount)

  def offset(xAmount: TileSize, yAmount: TileSize): TileCoordinate = offset(xAmount.toInt, yAmount.toInt)

  def offset(amount: TileSize): TileCoordinate = offset(amount.toInt, amount.toInt)

  private def xAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate] = {
    (x to other.x by step).map(TileCoordinate(_, y)).toSet
  }

  private def yAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate] = {
    (y to other.y by step).map(TileCoordinate(x, _)).toSet
  }

  def matrixOfCoordinatesTo(other: TileCoordinate, step: Int): Set[TileCoordinate] = {
    yAxisCoordsTo(other, step)
      .flatMap(_.xAxisCoordsTo(other, step))
  }

  def matrixOfCoordinatesTo(other: TileCoordinate): Set[TileCoordinate] = {
    matrixOfCoordinatesTo(other, 1)
  }

  def toMapCoordinate = MapCoordinate(x.toDouble, y.toDouble)
}

object TileCoordinate {
  val Max = Tile.Max + 1
  val Origin = TileCoordinate(0, 0)
  val End = TileCoordinate(Max.toInt, Max.toInt)
  val All = Origin.matrixOfCoordinatesTo(End)
  val AllEdge =
    // Top
    Origin.matrixOfCoordinatesTo(TileCoordinate(Max.toInt, 0)) ++
    // Bottom
    TileCoordinate(0, Max.toInt).matrixOfCoordinatesTo(TileCoordinate(Max.toInt, Max.toInt)) ++
    // Left
    Origin.matrixOfCoordinatesTo(TileCoordinate(0, Max.toInt)) ++
    // Right
    TileCoordinate(Max.toInt, 0).matrixOfCoordinatesTo(TileCoordinate(Max.toInt, Max.toInt))

  val MapOrigin = Tile.MapOrigin.toMapCoordinate
}