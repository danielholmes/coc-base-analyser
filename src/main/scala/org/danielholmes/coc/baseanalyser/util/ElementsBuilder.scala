package org.danielholmes.coc.baseanalyser.util

import org.danielholmes.coc.baseanalyser.model.{Element, Tile, Village}

object ElementsBuilder {
  def rectangle[T <: Element](origin: Tile, xTimes: Int, yTimes: Int, step: Int, builder: (Tile) => T): Set[T] = {
    ElementsBuilder.repeatX(origin, xTimes, step, builder) ++
      ElementsBuilder.repeatX(origin.offset(0, yTimes - 1), xTimes, step, builder) ++
      ElementsBuilder.repeatY(origin.offset(0, step), yTimes - 2, step, builder) ++
      ElementsBuilder.repeatY(origin.offset(xTimes - 1, step), yTimes - 2, step, builder)
  }

  def repeatX[T <: Element](origin: Tile, times: Int, step: Int, builder: (Tile) => T): Set[T] = {
    Range(0, times)
      .map(origin.x + _ * step)
      .map(Tile(_, origin.y))
      .map(builder.apply)
      .toSet
  }

  private def repeatY[T <: Element](origin: Tile, times: Int, step: Int, builder: (Tile) => T): Set[T] = {
    Range(0, times)
      .map(origin.y + _ * step)
      .map(Tile(origin.x, _))
      .map(builder.apply)
      .toSet
  }

  def fromString[T <: Element](input: String, origin: Tile, builder: (Tile) => T): Set[T] = {
    input.split("\n")
      .zipWithIndex
      .flatMap(row => {
        row._1
          .zipWithIndex
          .filter(_._1 != ' ')
          .map(col => builder.apply(Tile(origin.x + col._2, origin.y + row._2)))
      })
      .toSet
  }

  def villageFromString(input: String, origin: Tile, builder: (Tile) => Element): Village = {
    Village(fromString(input, origin, builder))
  }
}
