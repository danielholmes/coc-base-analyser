package org.danielholmes.coc.baseanalyser.model

case class Tile(x: Int, y: Int) {
  import Tile._

  require((0 to Max.toInt).contains(x), s"Tile.x ($x) must be in [0:$Max]")
  require((0 to Max.toInt).contains(y), s"Tile.y ($y) must be in [0:$Max]")

  lazy val isWithinMap = AllInMap.contains(this)

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

  lazy val toMapCoordinate = TileCoordinate(x, y)

  def offset(xDiff: TileSize, yDiff: TileSize): Tile = {
    offset(xDiff.toInt, yDiff.toInt)
  }

  def offset(xDiff: Int, yDiff: Int): Tile = {
    Tile(x + xDiff, y + yDiff)
  }
}

object Tile {
  private val MapSize = TileSize(44)
  private val OutsideBorder = TileSize(1) // TODO: Should maybe be the full 3 that clash natively uses

  val Max = MapSize + (OutsideBorder * 2) - 1

  val Origin = Tile(0, 0)
  val End = Tile(Max, Max)
  val All = Origin.matrixOfTilesTo(End)

  val MapOrigin = Origin.offset(OutsideBorder, OutsideBorder)
  val MapEnd = MapOrigin.offset(MapSize - 1, MapSize - 1)
  val AllInMap = MapOrigin.matrixOfTilesTo(End)

  def fromCoordinate(mapTileCoordinate: TileCoordinate): Tile = {
    Tile(mapTileCoordinate.x, mapTileCoordinate.y)
  }

  def apply(xSize: TileSize, ySize: TileSize): Tile = {
    Tile(xSize.toInt, ySize.toInt)
  }
}
