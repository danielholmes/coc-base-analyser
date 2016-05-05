package org.danielholmes.coc.baseanalyser.model.defense

import org.danielholmes.coc.baseanalyser.model.Angle
import org.scalactic.anyvals.PosZInt

trait AirSweeperAngle {
  override def toString: String = s"AirSweeperAngle($toInt)"

  def toInt: Int

  def toAngle: Angle
}

object AirSweeperAngle {
  implicit def widenToPosZInt(angle: AirSweeperAngle): PosZInt = PosZInt.from(angle.toInt).get

  implicit def apply(angle: Angle): AirSweeperAngle = {
    if (angle.degrees % 45 != 0) {
      throw new IllegalArgumentException(s"$angle should be increments of 45")
    }

    AirSweeperAngleImpl(angle)
  }

  private case class AirSweeperAngleImpl(private val value: Angle) extends AirSweeperAngle {
    val toInt = Math.round(value.degrees).toInt
    val toAngle = value
  }
}
