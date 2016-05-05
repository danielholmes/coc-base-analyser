package org.danielholmes.coc.baseanalyser.model

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.danielholmes.coc.baseanalyser.util.Memo2
import org.scalactic.anyvals.{PosInt, PosZDouble, PosZInt}

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait TileCoordinate {
  val x: PosZInt
  val y: PosZInt
  def distanceTo(other: FloatMapCoordinate): PosZDouble
  def offset(xAmount: Int, yAmount: Int): TileCoordinate
  def offset(xAmount: Double, yAmount: Double): FloatMapCoordinate
  def matrixOfCoordinatesTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]
  def matrixOfCoordinatesTo(other: TileCoordinate): Set[TileCoordinate]
  def xAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]
  def yAxisCoordsTo(other: TileCoordinate, step: PosInt): Set[TileCoordinate]
  def neighbours: Set[TileCoordinate]
}

object TileCoordinate {
  private val applyMemo = Memo2[PosZInt, PosZInt, TileCoordinate](TileCoordinateImpl)
  def apply(x: PosZInt, y: PosZInt): TileCoordinate = applyMemo.apply(x, y)

  // TODO: Remove this to try and reduce new mapcoordinate creation
  implicit def widenToMapCoordinate(coord: TileCoordinate): FloatMapCoordinate = FloatMapCoordinate(coord.x, coord.y)

  implicit def widenToVector2D(coord: TileCoordinate): Vector2D = new Vector2D(coord.x, coord.y)

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

  val MapOrigin: TileCoordinate = Tile.MapOrigin

  private case class TileCoordinateImpl(x: PosZInt, y: PosZInt) extends TileCoordinate {
    require((0 to MaxCoordinate).contains(x.toInt), s"TileCoordinates.x must be >= 0 <= $MaxCoordinate, given: $x")
    require((0 to MaxCoordinate).contains(y.toInt), s"TileCoordinates.y must be >= 0 <= $MaxCoordinate, given: $y")

    def distanceTo(other: FloatMapCoordinate): PosZDouble = PosZDouble.from(Math.hypot(x - other.x, y - other.y)).get

    def offset(xAmount: Int, yAmount: Int): TileCoordinate = {
      TileCoordinate(PosZInt.from(x + xAmount).get, PosZInt.from(y + yAmount).get)
    }

    def offset(xAmount: Double, yAmount: Double): FloatMapCoordinate = {
      FloatMapCoordinate(PosZDouble.from(x + xAmount).get, PosZDouble.from(y + yAmount).get)
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

    lazy val neighbours: Set[TileCoordinate] = {
      Set(up, down, left, right).flatMap(_.iterator)
    }

    private lazy val right: Option[TileCoordinate] = Some(x).filter(_ < TileCoordinate.MaxCoordinate).map(x => offset(1, 0))
    private lazy val left: Option[TileCoordinate] = Some(x).filter(_ > 0).map(x => offset(-1, 0))
    private lazy val down: Option[TileCoordinate] = Some(y).filter(_ < TileCoordinate.MaxCoordinate).map(y => offset(0, 1))
    private lazy val up: Option[TileCoordinate] = Some(y).filter(_ > 0).map(y => offset(0, -1))

    override val toString = s"TileCoordinate($x, $y)"
  }
}
