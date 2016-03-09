package org.danielholmes.coc.baseanalyser.model

case class Tile(x: Int, y: Int) {
  require(x >= 0 && x <= Tile.Max, s"Tile.x must be >= 0 and <= ${Tile.Max}")
  require(y >= 0 && y <= Tile.Max, s"Tile.y must be >= 0 and <= ${Tile.Max}")

  def matrixOfTilesTo(other: Tile): Set[Tile] = {
    matrixOfTilesTo(other, 1)
  }

  def matrixOfTilesTo(other: Tile, step: Int): Set[Tile] = {
    (x to other.x by step).flatMap(newX => (y to other.y by step).map(newY => Tile(newX, newY))).toSet
  }

  def matrixOfTilesInDirection(width: TileSize, height: TileSize): Set[Tile] = {
    matrixOfTilesTo(Tile(x + width.toInt - 1, y + height.toInt - 1))
  }

  lazy val allCoordinates = {
    toMapCoordinate.matrixOfCoordinatesTo(toMapCoordinate.offset(1, 1))
  }

  lazy val toMapCoordinate = MapTileCoordinate(x, y)

  def offset(xDiff: Int, yDiff: Int) = {
    Tile(x + xDiff, y + yDiff)
  }
}

object Tile {
  val Max = 43

  val Origin = Tile(0, 0)
  val End = Tile(MapTileCoordinate.End.x - 1, MapTileCoordinate.End.y - 1)
  val All = Origin.matrixOfTilesTo(End)

  def fromCoordinate(mapTileCoordinate: MapTileCoordinate): Tile = {
    Tile(mapTileCoordinate.x, mapTileCoordinate.y)
  }
}
