package org.danielholmes.coc.baseanalyser.model

object TileCoordinate {
  val Max = 44
  val Origin = TileCoordinate(0, 0)
  val Middle: TileCoordinate = TileCoordinate(Max / 2, Max / 2)
  val End = TileCoordinate(Max, Max)
  val All = Origin.matrixOfCoordinatesTo(End)
}

case class TileCoordinate(x: Int, y: Int) {
  require((0 to TileCoordinate.Max).contains(x), s"TileCoordinates.x must be >= 0 <= ${TileCoordinate.Max}, given: $x")
  require((0 to TileCoordinate.Max).contains(y), s"TileCoordinates.y must be >= 0 <= ${TileCoordinate.Max}, given: $y")

  def distanceTo(other: TileCoordinate): Double = distanceTo(other.toMapCoordinate)

  def distanceTo(other: MapCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }

  def offsetX(amount: Int): TileCoordinate = offset(amount, 0)

  def offsetY(amount: Int): TileCoordinate = offset(0, amount)

  def offset(xAmount: Int, yAmount: Int): TileCoordinate = TileCoordinate(x + xAmount, y + yAmount)

  def offset(xAmount: TileSize, yAmount: TileSize): TileCoordinate = offset(xAmount.toInt, yAmount.toInt)

  def xAxisCoordsTo(other: TileCoordinate): Set[TileCoordinate] = xAxisCoordsTo(other, 1)

  def yAxisCoordsTo(other: TileCoordinate): Set[TileCoordinate] = yAxisCoordsTo(other, 1)

  def xAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate] = {
    (x to other.x by step).map(TileCoordinate(_, y)).toSet
  }

  def yAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate] = {
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
