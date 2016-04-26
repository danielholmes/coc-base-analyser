package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.Memo2

import org.scalactic.anyvals.{PosInt, PosZInt, PosZDouble}

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait TileCoordinate {
  val x: PosZInt
  val y: PosZInt
  def distanceTo(other: TileCoordinate): PosZDouble
  def distanceTo(other: MapCoordinate): PosZDouble
  def offset(xAmount: Int, yAmount: Int): TileCoordinate
  def offset(xAmount: Double, yAmount: Double): MapCoordinate
  def matrixOfCoordinatesTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]
  def matrixOfCoordinatesTo(other: TileCoordinate): Set[TileCoordinate]
  def xAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]
  def yAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]

  // TODO: See if can replace with implicit widening (e.g. see PosInt, etc)
  def toMapCoordinate: MapCoordinate
}

object TileCoordinate {
  private val applyMemo = Memo2[PosZInt, PosZInt, TileCoordinate](TileCoordinateImpl)
  def apply(x: PosZInt, y: PosZInt): TileCoordinate = applyMemo.apply(x, y)

  val MaxCoordinate = PosInt.from(Tile.MaxCoordinate + 1).get
  val Origin = TileCoordinate(0, 0)
  val End = TileCoordinate(MaxCoordinate, MaxCoordinate)
  val All = Origin.matrixOfCoordinatesTo(End)
  val AllEdge =
    // Top
    Origin.matrixOfCoordinatesTo(TileCoordinate(MaxCoordinate, 0)) ++
    // Bottom
    TileCoordinate(0, MaxCoordinate).matrixOfCoordinatesTo(TileCoordinate(MaxCoordinate, MaxCoordinate)) ++
    // Left
    Origin.matrixOfCoordinatesTo(TileCoordinate(0, MaxCoordinate)) ++
    // Right
    TileCoordinate(MaxCoordinate, 0).matrixOfCoordinatesTo(TileCoordinate(MaxCoordinate, MaxCoordinate))

  val MapOrigin = Tile.MapOrigin.toTileCoordinate

  private case class TileCoordinateImpl(x: PosZInt, y: PosZInt) extends TileCoordinate {
    require((0 to MaxCoordinate).contains(x.toInt), s"TileCoordinates.x must be >= 0 <= $MaxCoordinate, given: $x")
    require((0 to MaxCoordinate).contains(y.toInt), s"TileCoordinates.y must be >= 0 <= $MaxCoordinate, given: $y")

    def distanceTo(other: TileCoordinate): PosZDouble = distanceTo(other.toMapCoordinate)

    def distanceTo(other: MapCoordinate): PosZDouble = toMapCoordinate.distanceTo(other)

    def offset(xAmount: Int, yAmount: Int): TileCoordinate = {
      TileCoordinate(PosZInt.from(x + xAmount).get, PosZInt.from(y + yAmount).get)
    }

    def offset(xAmount: Double, yAmount: Double): MapCoordinate = {
      MapCoordinate(PosZDouble.from(x + xAmount).get, PosZDouble.from(y + yAmount).get)
    }

    def xAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate] = {
      (x to other.x by step).map((newX: Int) => TileCoordinate(PosZInt.from(newX).get, PosZInt.from(y).get)).toSet
    }

    def yAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate] = {
      (y to other.y by step).map((newY: Int) => TileCoordinate(PosZInt.from(x).get, PosZInt.from(newY).get)).toSet
    }

    def matrixOfCoordinatesTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate] = {
      yAxisCoordsTo(other, step)
        .flatMap(_.xAxisCoordsTo(other, step))
    }

    def matrixOfCoordinatesTo(other: TileCoordinate): Set[TileCoordinate] = matrixOfCoordinatesTo(other, 1)

    def toMapCoordinate = MapCoordinate(x, y)

    override val toString = s"TileCoordinate($x, $y)"
  }
}