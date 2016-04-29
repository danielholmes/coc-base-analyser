package org.danielholmes.coc.baseanalyser.util

import org.danielholmes.coc.baseanalyser.model.{Element, Tile, Village, Wall}
import org.scalactic.anyvals.{PosInt, PosZInt}

object ElementsBuilder {
  def elementFence(origin: Tile, width: PosInt, height: PosInt): Set[Element] = {
    wallFence(origin, width, height).map(_.asInstanceOf[Element])
  }

  def wallFence(origin: Tile, width: PosInt, height: PosInt): Set[Wall] = {
    rectangle(origin, width, height, 1, Wall(1, _))
  }

  def rectangle[T <: Element](origin: Tile, xTimes: PosInt, yTimes: PosInt, step: PosInt, builder: (Tile) => T): Set[T] = {
    ElementsBuilder.repeatX(origin, xTimes, step, builder) ++
      ElementsBuilder.repeatX(origin.offset(0, (yTimes - 1) * step), xTimes, step, builder) ++
      ElementsBuilder.repeatY(origin.offset(0, step), PosInt.from(yTimes - 2).get, step, builder) ++
      ElementsBuilder.repeatY(origin.offset((xTimes - 1) * step, step), PosInt.from(yTimes - 2).get, step, builder)
  }

  def repeatX[T <: Element](origin: Tile, times: PosInt, step: PosInt, builder: (Tile) => T): Set[T] = {
    Range(0, times)
      .map(origin.x + _ * step)
      .map((x: Int) => Tile(PosZInt.from(x).get, origin.y))
      .map(builder.apply)
      .toSet
  }

  private def repeatY[T <: Element](origin: Tile, times: PosInt, step: PosInt, builder: (Tile) => T): Set[T] = {
    Range(0, times)
      .map(origin.y + _ * step)
      .map((y: Int) => Tile(origin.x, PosZInt.from(y).get))
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
          .map(col => builder.apply(Tile(PosZInt.from(origin.x + col._2).get, PosZInt.from(origin.y + row._2).get)))
      })
      .toSet
  }

  def villageFromString(input: String, origin: Tile, builder: (Tile) => Element): Village = {
    Village(fromString(input, origin, builder))
  }
}
