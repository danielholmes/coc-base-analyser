package org.danielholmes.coc.baseanalyser.model

object TileCoordinate {
  val Max: Int = 44
  val Middle: TileCoordinate = TileCoordinate(Max / 2, Max / 2)
  val All = (1 to (Max * Max)).map(i => TileCoordinate(i / Max, i % Max))
}

case class TileCoordinate(x: Int, y: Int) {
  require(x <= TileCoordinate.Max && x >= 0, "TileCoordinates.x must be >= 0 <= " + TileCoordinate.Max)
  require(y <= TileCoordinate.Max && y >= 0, "TileCoordinates.y must be >= 0 <= " + TileCoordinate.Max)

  def distanceTo(other: TileCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }

  def offsetX(amount: Int): TileCoordinate = TileCoordinate(x + amount, y)

  def offsetY(amount: Int): TileCoordinate = TileCoordinate(x, y + amount)

  def xAxisCoordsTo(other: TileCoordinate): Set[TileCoordinate] = {
    (x to other.x).map(TileCoordinate(_, y)).toSet
  }

  def yAxisCoordsTo(other: TileCoordinate): Set[TileCoordinate] = {
    (y to other.y).map(TileCoordinate(x, _)).toSet
  }
}
