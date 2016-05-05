package org.danielholmes.coc.baseanalyser.model.range

import org.danielholmes.coc.baseanalyser.model.{Angle, FloatMapCoordinate, Tile, TileCoordinate}
import org.scalactic.anyvals.PosDouble

case class BlindSpotSectorElementRange(
  centre: FloatMapCoordinate,
  innerSize: PosDouble,
  outerSize: PosDouble,
  angle: Angle,
  angleSize: Angle
) extends ElementRange {
  private val minAngle: Angle = angle - angleSize / 2
  private val maxAngle: Angle = angle + angleSize / 2

  def contains(testCoordinate: FloatMapCoordinate): Boolean = {
    val distance = testCoordinate.distanceTo(centre)
    val testAngle = Angle.atan2(centre.y - testCoordinate.y, centre.x - testCoordinate.x) - Angle.Quarter
    distance >= innerSize && distance < outerSize && minAngle.isLeftOf(testAngle) && maxAngle.isRightOf(testAngle)
  }
}
