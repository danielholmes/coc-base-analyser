package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.{Memo2, PathFinder}

import org.scalactic.anyvals.{PosInt, PosZInt, PosZDouble}

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait Tile {
  val x: PosZInt
  val y: PosZInt
  val isWithinMap: Boolean
  val touchingTiles: Set[Tile]
  val centre: MapCoordinate
  def distanceTo(other: Tile): PosZDouble
  def centreDistanceTo(other: Tile): PosZDouble
  def manhattanDistanceTo(other: Tile): PosZInt
  def shortestTilePathTo(other: Tile, wallTiles: Set[Tile]): Option[List[Tile]]
  def matrixOfTilesTo(other: Tile): Set[Tile]
  def matrixOfTilesTo(other: Tile, step: PosInt): Set[Tile]
  // Should prob allow 0 too, but not negative obviously
  def matrixOfTilesInDirection(width: PosInt, height: PosInt): Set[Tile]
  val allCoordinates: Set[TileCoordinate]
  def offset(xDiff: Int, yDiff: Int): Tile
}

object Tile {
  private val applyMemo = Memo2[PosZInt, PosZInt, Tile](TileImpl)

  def apply(x: PosZInt, y: PosZInt): Tile = applyMemo.apply(x, y)

  private val MapSize: PosInt = 44
  private val OutsideBorder: PosInt = 1 // TODO: Should maybe be the full 3 that clash natively uses

  val MaxCoordinate: PosInt = PosInt.from(MapSize + (OutsideBorder * 2) - 1).get

  val Origin = Tile(0, 0)
  val End = Tile(MaxCoordinate, MaxCoordinate)
  val All = Origin.matrixOfTilesTo(End)

  val MapOrigin = Origin.offset(OutsideBorder, OutsideBorder)
  val MapEnd = MapOrigin.offset(MapSize - 1, MapSize - 1)
  val AllInMap = MapOrigin.matrixOfTilesTo(End)
  val AllOutsideMap = All -- AllInMap

  implicit def widenToTileCoordinate(tile: Tile): TileCoordinate = TileCoordinate(tile.x, tile.y)

  def fromCoordinate(tileCoordinate: TileCoordinate): Tile = {
    Tile(tileCoordinate.x, tileCoordinate.y)
  }

  private case class TileImpl(x: PosZInt, y: PosZInt) extends Tile {
    require((0 to MaxCoordinate).contains(x.toInt), s"Tile.x ($x) must be in [0:$MaxCoordinate]")
    require((0 to MaxCoordinate).contains(y.toInt), s"Tile.y ($y) must be in [0:$MaxCoordinate]")

    lazy val isWithinMap = AllInMap.contains(this)

    lazy val touchingTiles = {
      Range.inclusive(x - 1, x + 1)
        .flatMap(tileX => Range.inclusive(y - 1, y + 1).map(tileY => Tuple2(tileX, tileY)))
        .filter(possible => possible._1 >= 0 && possible._1 <= Tile.MaxCoordinate && possible._2 >= 0 && possible._2 <= Tile.MaxCoordinate)
        .map(coord => Tile(PosZInt.from(coord._1).get, PosZInt.from(coord._2).get))
        .filterNot(_ == this)
        .toSet
    }

    lazy val centre = MapCoordinate(PosZDouble.from(x + 0.5).get, PosZDouble.from(y + 0.5).get)

    def distanceTo(other: Tile): PosZDouble = {
      allCoordinates.flatMap(otherCoord => other.allCoordinates.map(_.distanceTo(otherCoord))).min
    }

    def centreDistanceTo(other: Tile): PosZDouble = centre.distanceTo(other.centre)

    def manhattanDistanceTo(other: Tile): PosZInt = PosZInt.from(Math.abs(other.x - x) + Math.abs(other.y - y)).get

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

    def matrixOfTilesTo(other: Tile, step: PosInt): Set[Tile] = {
      (x to other.x by step).flatMap(newX => (y to other.y by step).map(newY => Tile(PosZInt.from(newX).get, PosZInt.from(newY).get))).toSet
    }

    def matrixOfTilesInDirection(width: PosInt, height: PosInt): Set[Tile] = {
      matrixOfTilesTo(Tile(PosZInt.from(x + width - 1).get, PosZInt.from(y + height - 1).get))
    }

    lazy val allCoordinates = toTileCoordinate.matrixOfCoordinatesTo(toTileCoordinate.offset(1, 1))

    private lazy val toTileCoordinate = widenToTileCoordinate(this)

    def offset(xDiff: Int, yDiff: Int): Tile = Tile(PosZInt.from(x + xDiff).get, PosZInt.from(y + yDiff).get)

    override lazy val toString = s"Tile(${x.toInt}, ${y.toInt})"
  }
}
