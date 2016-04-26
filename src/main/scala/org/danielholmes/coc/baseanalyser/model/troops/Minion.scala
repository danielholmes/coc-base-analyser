package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Element, MapCoordinate}
import org.scalactic.anyvals.PosDouble

object Minion {
  val Range = PosDouble(0.75)
  private val DiagonalRange = Math.sqrt(Range / 2)

  def getAttackPositions(element: Element): Set[MapCoordinate] = {
    element.hitBlock.allCoordinates.map(_.toMapCoordinate) ++
      element.hitBlock.leftSide.map(_.offset(-Range, 0)) ++
      element.hitBlock.rightSide.map(_.offset(Range, 0)) ++
      element.hitBlock.topSide.map(_.offset(0, -Range)) ++
      element.hitBlock.bottomSide.map(_.offset(0, Range)) +
      element.hitBlock.topLeft.offset(-DiagonalRange, -DiagonalRange) +
      element.hitBlock.topRight.offset(DiagonalRange, -DiagonalRange) +
      element.hitBlock.bottomLeft.offset(-DiagonalRange, DiagonalRange) +
      element.hitBlock.bottomRight.offset(DiagonalRange, DiagonalRange)
  }
}
