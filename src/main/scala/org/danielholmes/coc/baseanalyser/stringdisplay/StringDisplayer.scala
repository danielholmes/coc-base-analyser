package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._

class StringDisplayer {
  def build(base: Village): String = {
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
    val char = characterForElement(element)
    var newList = current
    for (row <- 1 to element.size.toInt) {
      for (col <- 1 to element.size.toInt) {
        val rowIndex = row - 1 + element.coordinate.y
        val colIndex = col - 1 + element.coordinate.x
        newList = newList.patch(rowIndex, Seq(newList(rowIndex).patch(colIndex, Seq(char), 1)), 1)
      }
    }
    newList
  }

  private def characterForElement(element: Element): Char = {
    element match {
      case _: ArcherTower => 'A'
      case _: BarbarianKing => 'B'
      case _: AirDefense => 'D'
      case _: Cannon => 'C'
      case _: InfernoTower => 'I'
      case _: Mortar => 'M'
      case _: ArcherQueen => 'Q'
      case _: AirSweeper => 'S'
      case _: TeslaTower => 'T'
      case _: WizardTower => 'W'
      case _: XBow => 'X'
      case _: ClanCastle => '@'
      case _: TownHall => '#'
      case _: Wall => '+'
      case _ =>
        val possibles = Vector('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '*', '/', '\\', '<', '>')
        possibles.toVector(element.toString.hashCode % possibles.size)
    }
  }

  private def drawWalls(current: List[List[Char]]): List[List[Char]] = {
    (horizontalWall :: verticalWall(current)) :+ horizontalWall
  }

  private def verticalWall(current: List[List[Char]]): List[List[Char]] = {
    if (current.isEmpty) return current
    (('|' :: current.head) :+ '|') +: verticalWall(current.tail)
  }

  private val horizontalWall: List[Char] = ('+' :: List.fill[Char](TileCoordinate.Max) { '-' }) :+ '+'
}
