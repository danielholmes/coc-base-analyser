package org.danielholmes.coc.baseanalyser.util

import org.danielholmes.coc.baseanalyser.model.{Element, Tile, Village}

object ElementsBuilder {
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
