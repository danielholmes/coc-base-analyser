package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._

class StringAttackPlayementsDisplayer {
  def build(village: Village): String = {
    build(
      village.coordinatesAllowedToDropTroop.toSeq,
      List.fill[Char](MapTileCoordinate.Max + 1, MapTileCoordinate.Max + 1) { ' ' }
    ).map(_ :+ "\n")
      .map(_.mkString(""))
      .mkString("")
  }

  private def build(coords: Seq[MapTileCoordinate], current: List[List[Char]]): List[List[Char]] = {
    if (coords.isEmpty) return current
    build(
      coords.tail,
      drawCoord(coords.head, current)
    )
  }

  private def drawCoord(coord: MapTileCoordinate, current: List[List[Char]]): List[List[Char]] = {
    current.patch(coord.y, Seq(current(coord.y).patch(coord.x, Seq('+'), 1)), 1)
  }
}
