package org.danielholmes.coc.baseanalyser.model

object MapTileCoordinate {
  val Max = Tile.Max + 1
  val Origin = MapTileCoordinate(0, 0)
  val End = MapTileCoordinate(Max, Max)
  val All = Origin.matrixOfCoordinatesTo(End)
}

case class MapTileCoordinate(x: Int, y: Int) {
  require((0 to MapTileCoordinate.Max).contains(x), s"TileCoordinates.x must be >= 0 <= ${MapTileCoordinate.Max}, given: $x")
  require((0 to MapTileCoordinate.Max).contains(y), s"TileCoordinates.y must be >= 0 <= ${MapTileCoordinate.Max}, given: $y")

  def distanceTo(other: MapTileCoordinate): Double = distanceTo(other.toMapCoordinate)

  def distanceTo(other: MapCoordinate): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }

  def offset(xAmount: Int, yAmount: Int): MapTileCoordinate = MapTileCoordinate(x + xAmount, y + yAmount)

  def offset(xAmount: TileSize, yAmount: TileSize): MapTileCoordinate = offset(xAmount.toInt, yAmount.toInt)

  private def xAxisCoordsTo(other: MapTileCoordinate, step: Int): Set[MapTileCoordinate] = {
    (x to other.x by step).map(MapTileCoordinate(_, y)).toSet
  }

  private def yAxisCoordsTo(other: MapTileCoordinate, step: Int): Set[MapTileCoordinate] = {
    (y to other.y by step).map(MapTileCoordinate(x, _)).toSet
  }

  def matrixOfCoordinatesTo(other: MapTileCoordinate, step: Int): Set[MapTileCoordinate] = {
    yAxisCoordsTo(other, step)
      .flatMap(_.xAxisCoordsTo(other, step))
  }

  def matrixOfCoordinatesTo(other: MapTileCoordinate): Set[MapTileCoordinate] = {
    matrixOfCoordinatesTo(other, 1)
  }

  def toMapCoordinate = MapCoordinate(x.toDouble, y.toDouble)
}
