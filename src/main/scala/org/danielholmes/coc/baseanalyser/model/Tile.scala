package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.{Memo2, PathFinder}

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait Tile {
  val x: Int
  val y: Int
  val isWithinMap: Boolean
  val touchingTiles: Set[Tile]
  val centre: MapCoordinate
  def distanceTo(other: Tile): Double
  def centreDistanceTo(other: Tile): Double
  def manhattanDistanceTo(other: Tile): Int
  def shortestTilePathTo(other: Tile, wallTiles: Set[Tile]): Option[List[Tile]]
  def matrixOfTilesTo(other: Tile): Set[Tile]
  def matrixOfTilesTo(other: Tile, step: Int): Set[Tile]
  def matrixOfTilesInDirection(width: TileSize, height: TileSize): Set[Tile]
  val toBlock: Block
  val allCoordinates: Set[TileCoordinate]
  val toMapCoordinate: TileCoordinate
  def offset(xDiff: Int, yDiff: Int): Tile
}

object Tile {
  private val applyMemo = Memo2[Int, Int, Tile](TileImpl)

  def apply(x: Int, y: Int): Tile = applyMemo.apply(x, y)

  private val MapSize = TileSize(44)
  private val OutsideBorder = TileSize(1) // TODO: Should maybe be the full 3 that clash natively uses

  val MaxCoordinate: Int = MapSize.toInt + (OutsideBorder.toInt * 2) - 1

  val Origin = Tile(0, 0)
  val End = Tile(MaxCoordinate.toInt, MaxCoordinate.toInt)
  val All = Origin.matrixOfTilesTo(End)

  val MapOrigin = Origin.offset(OutsideBorder.toInt, OutsideBorder.toInt)
  val MapEnd = MapOrigin.offset(MapSize.toInt - 1, MapSize.toInt - 1)
  val AllInMap = MapOrigin.matrixOfTilesTo(End)
  val AllOutsideMap = All -- AllInMap

  def fromCoordinate(mapTileCoordinate: TileCoordinate): Tile = {
    Tile(mapTileCoordinate.x, mapTileCoordinate.y)
  }

  private case class TileImpl(x: Int, y: Int) extends Tile {
    require((0 to MaxCoordinate.toInt).contains(x), s"Tile.x ($x) must be in [0:$MaxCoordinate]")
    require((0 to MaxCoordinate.toInt).contains(y), s"Tile.y ($y) must be in [0:$MaxCoordinate]")

    lazy val isWithinMap = AllInMap.contains(this)

    lazy val touchingTiles = {
      Range.inclusive(x - 1, x + 1)
        .flatMap(tileX => Range.inclusive(y - 1, y + 1).map(tileY => Tuple2(tileX, tileY)))
        .filter(possible => possible._1 >= 0 && possible._1 <= Tile.MaxCoordinate.toInt && possible._2 >= 0 && possible._2 <= Tile.MaxCoordinate.toInt)
        .map(coord => Tile(coord._1, coord._2))
        .filterNot(_ == this)
        .toSet
    }

    lazy val centre = MapCoordinate(x + 0.5, y + 0.5)

    def distanceTo(other: Tile): Double = {
      allCoordinates.flatMap(otherCoord => other.allCoordinates.map(_.distanceTo(otherCoord))).min
    }

    def centreDistanceTo(other: Tile): Double = centre.distanceTo(other.centre)

    def manhattanDistanceTo(other: Tile): Int = Math.abs(other.x - x) + Math.abs(other.y - y)

    def shortestTilePathTo(other: Tile, wallTiles: Set[Tile]): Option[List[Tile]] = {
      PathFinder.apply(
        this,
        other,
        (subject: Tile) => subject.touchingTiles.diff(wallTiles).toList,
        (tile1: Tile, tile2: Tile) => tile1.centreDistanceTo(tile2).toFloat,
        (tile1: Tile, tile2: Tile) => 1
      )
    }

    def matrixOfTilesTo(other: Tile): Set[Tile] = matrixOfTilesTo(other, 1)

    def matrixOfTilesTo(other: Tile, step: Int): Set[Tile] = {
      (x to other.x by step).flatMap(newX => (y to other.y by step).map(newY => Tile(newX, newY))).toSet
    }

    def matrixOfTilesInDirection(width: TileSize, height: TileSize): Set[Tile] = {
      matrixOfTilesTo(Tile(x + width.toInt - 1, y + height.toInt - 1))
    }

    lazy val toBlock = Block(this, TileSize(1))

    lazy val allCoordinates = toMapCoordinate.matrixOfCoordinatesTo(toMapCoordinate.offset(1, 1))

    lazy val toMapCoordinate = TileCoordinate(x, y)

    def offset(xDiff: Int, yDiff: Int): Tile = Tile(x + xDiff, y + yDiff)

    override lazy val toString = s"Tile($x, $y)"
  }
}
