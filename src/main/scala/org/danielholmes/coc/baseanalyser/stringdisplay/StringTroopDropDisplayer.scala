package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._

import scala.annotation.tailrec

class StringTroopDropDisplayer {
  def build(village: Village): String = {
    build(
      village.coordinatesAllowedToDropTroop.toSeq,
      List.fill[Char](TileCoordinate.MaxCoordinate + 1, TileCoordinate.MaxCoordinate + 1) { ' ' }
    ).map(_ :+ "\n")
      .map(_.mkString(""))
      .mkString("")
  }

  @tailrec
  private def build(coords: Seq[TileCoordinate], current: List[List[Char]]): List[List[Char]] = {
    coords match {
      case Nil => current
      case head :: tail => build(tail, drawCoord(head, current))
    }
  }

  private def drawCoord(coord: TileCoordinate, current: List[List[Char]]): List[List[Char]] = {
    current.patch(coord.y, Seq(current(coord.y).patch(coord.x, Seq('+'), 1)), 1)
  }
}
