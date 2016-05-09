package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.Memo2

import org.scalactic.anyvals.{PosInt, PosZInt, PosZDouble}

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait Tile {
  val x: PosZInt
  val y: PosZInt
  val isWithinMap: Boolean
  val neighbours: Set[Tile]
  val centre: FloatMapCoordinate
  def distanceTo(other: Tile): PosZDouble
  def centreDistanceTo(other: Tile): PosZDouble
  def manhattanDistanceTo(other: Tile): PosZInt
  def matrixOfTilesTo(other: Tile): Set[Tile]
  def matrixOfTilesTo(other: Tile, step: PosInt): Set[Tile]
  def rectangleTo(other: Tile): Set[Tile]
  // Should prob allow 0 too, but not negative obviously
  def matrixOfTilesInDirection(width: PosInt, height: PosInt): Set[Tile]
  val allCoordinates: Set[TileCoordinate]
  def offset(xDiff: Int, yDiff: Int): Tile
}

object Tile {
  private val applyMemo = Memo2[PosZInt, PosZInt, Tile](TileImpl)

  def apply(x: PosZInt, y: PosZInt): Tile = applyMemo.apply(x, y)

  val MapSize = PosInt(44)
  val OutsideBorder = PosInt(3)

  val MaxCoordinate = PosInt.from(MapSize + (OutsideBorder * 2) - 1).get

  val Origin = Tile(0, 0)
  val End = Tile(MaxCoordinate, MaxCoordinate)
  val All = Origin.matrixOfTilesTo(End)

  val MapOrigin = Origin.offset(OutsideBorder, OutsideBorder)
  val MapEnd = MapOrigin.offset(MapSize - 1, MapSize - 1)
  val AllInMap = MapOrigin.matrixOfTilesTo(End)
  val AllOutsideMap = All -- AllInMap
  val InnerBorder = MapOrigin.offset(-1, -1).rectangleTo(MapEnd.offset(1, 1))
  val AllNotTouchingMap = AllOutsideMap -- InnerBorder

  implicit def widenToTileCoordinate(tile: Tile): TileCoordinate = TileCoordinate(tile.x, tile.y)

  def fromCoordinate(tileCoordinate: TileCoordinate): Tile = {
    Tile(tileCoordinate.x, tileCoordinate.y)
  }

  private case class TileImpl(x: PosZInt, y: PosZInt) extends Tile {
    require((0 to MaxCoordinate).contains(x.toInt), s"Tile.x ($x) must be in [0:$MaxCoordinate]")
    require((0 to MaxCoordinate).contains(y.toInt), s"Tile.y ($y) must be in [0:$MaxCoordinate]")

    lazy val isWithinMap = AllInMap.contains(this)

    lazy val neighbours: Set[Tile] = {
      Set(up, down, left, right, upLeft, upRight, downLeft, downRight).flatMap(_.iterator)
    }

    private lazy val right = tryOffset(1, 0)
    private lazy val left = tryOffset(-1, 0)
    private lazy val down = tryOffset(0, 1)
    private lazy val up = tryOffset(0, -1)
    private lazy val upLeft = tryOffset(-1, -1)
    private lazy val upRight = tryOffset(1, -1)
    private lazy val downRight = tryOffset(1, 1)
    private lazy val downLeft = tryOffset(-1, 1)

    private def tryOffset(xOffset: Int, yOffset: Int): Option[Tile] = {
      val proposedX = x + xOffset
      val proposedY = y + yOffset
      if (proposedX >= 0 && proposedX <= Tile.MaxCoordinate && proposedY >= 0 && proposedY <= Tile.MaxCoordinate) {
        Some(Tile(PosZInt.from(proposedX).get, PosZInt.from(proposedY).get))
      } else {
        None
      }
    }

    lazy val centre = FloatMapCoordinate(PosZDouble.from(x + 0.5).get, PosZDouble.from(y + 0.5).get)

    def distanceTo(other: Tile): PosZDouble = {
      allCoordinates.flatMap(otherCoord => other.allCoordinates.map(_.distanceTo(otherCoord))).min
    }

    def centreDistanceTo(other: Tile): PosZDouble = centre.distanceTo(other.centre)

    def manhattanDistanceTo(other: Tile): PosZInt = PosZInt.from(Math.abs(other.x - x) + Math.abs(other.y - y)).get

    // Top + bottom + left + right
    def rectangleTo(other: Tile): Set[Tile] = this.matrixOfTilesTo(Tile(other.x, y), 1) ++
      Tile(x, other.y).matrixOfTilesTo(Tile(other.x, other.y), 1) ++
      this.matrixOfTilesTo(Tile(x, other.y)) ++
      Tile(other.x, y).matrixOfTilesTo(Tile(other.x, other.y))

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
