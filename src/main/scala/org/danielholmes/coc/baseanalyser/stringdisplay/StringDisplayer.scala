package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model.{TownHall, TileCoordinate, Base, Element}

class StringDisplayer {
  def build(base: Base): String = {
    drawWalls(drawElements(base.elements.toList, List.fill[Char](TileCoordinate.Max, TileCoordinate.Max) { ' ' }))
      .map(_ :+ "\n")
      .map(_.mkString(""))
      .mkString("")
  }

  private def drawElements(elements: List[Element], current: List[List[Char]]): List[List[Char]] = {
    if (elements.isEmpty) return current
    drawElements(elements.tail, drawElement(elements.head, current))
  }

  // TODO: Find functional solution
  private def drawElement(element: Element, current: List[List[Char]]): List[List[Char]] = {
    var newList = current
    for (row <- 1 to element.size.size) {
      for (col <- 1 to element.size.size) {
        val rowIndex = row - 1 + element.coordinate.y
        val colIndex = col - 1 + element.coordinate.x
        newList = newList.patch(rowIndex, Seq(newList(rowIndex).patch(colIndex, Seq(characterForElement(element)), 1)), 1)
      }
    }
    newList
  }

  private def characterForElement(element: Element): Char = {
    element match {
      case p: TownHall => 'T'
      case _ => 'A'
    }
  }

  private def drawWalls(current: List[List[Char]]): List[List[Char]] = {
    (horizontalWall :: verticalWall(current)) :+ horizontalWall
  }

  private def verticalWall(current: List[List[Char]]): List[List[Char]] = {
    if (current.isEmpty) return current
    (('|' :: current.head) :+ '|') +: verticalWall(current.tail)
  }

  private val horizontalWall: List[Char] = ('+' :: (List.fill[Char](TileCoordinate.Max) { '-' })) :+ '+'
}
