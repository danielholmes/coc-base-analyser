package org.danielholmes.coc.baseanalyser.model

import org.danielholmes.coc.baseanalyser.util.Memo2

// The various ceremony is for instance pooling. See
// http://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method
trait TileCoordinate {
  val x: Int
  val y: Int
  def distanceTo(other: TileCoordinate): Double
  def distanceTo(other: MapCoordinate): Double
  def offset(xAmount: Int, yAmount: Int): TileCoordinate
  def matrixOfCoordinatesTo(other: TileCoordinate, step: Int): Set[TileCoordinate]
  def matrixOfCoordinatesTo(other: TileCoordinate): Set[TileCoordinate]
  def xAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate]
  def yAxisCoordsTo(other: TileCoordinate, step: Int): Set[TileCoordinate]
  def toMapCoordinate: MapCoordinate
}

object TileCoordinate {
  private val applyMemo = Memo2[Int, Int, TileCoordinate](TileCoordinateImpl)
  def apply(x: Int, y: Int): TileCoordinate = applyMemo.apply(x, y)

  val Max = Tile.MaxCoordinate + 1
  val Origin = TileCoordinate(0, 0)
  val End = TileCoordinate(Max.toInt, Max.toInt)
  val All = Origin.matrixOfCoordinatesTo(End)
  val AllEdge =
    // Top
    Origin.matrixOfCoordinatesTo(TileCoordinate(Max.toInt, 0)) ++
    // Bottom
    TileCoordinate(0, Max.toInt).matrixOfCoordinatesTo(TileCoordinate(Max.toInt, Max.toInt)) ++
    // Left
    Origin.matrixOfCoordinatesTo(TileCoordinate(0, Max.toInt)) ++
    // Right
    TileCoordinate(Max.toInt, 0).matrixOfCoordinatesTo(TileCoordinate(Max.toInt, Max.toInt))

  val MapOrigin = Tile.MapOrigin.toMapCoordinate

  private case class TileCoordinateImpl(x: Int, y: Int) extends TileCoordinate {
    require((0 to Max.toInt).contains(x), s"TileCoordinates.x must be >= 0 <= $Max, given: $x")
    require((0 to Max.toInt).contains(y), s"TileCoordinates.y must be >= 0 <= $Max, given: $y")

    def distanceTo(other: TileCoordinate): Double = distanceTo(other.toMapCoordinate)

    def distanceTo(other: MapCoordinate): Double = {
      Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
    }

    def offset(xAmount: Int, yAmount: Int): TileCoordinate = TileCoordinate(x + xAmount, y + yAmount)

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

    override val toString = s"TileCoordinate($x, $y)"
  }
}