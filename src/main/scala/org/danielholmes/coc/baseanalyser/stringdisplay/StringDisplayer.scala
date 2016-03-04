package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._

class StringDisplayer {
  private val WallCorner = '+'
  private val WallHor = '-'
  private val WallVert = '|'
  private val WallChars = Set(WallCorner, WallHor, WallVert)

  private val Colors = Set(
    Console.MAGENTA,
    Console.BLUE,
    Console.CYAN,
    Console.GREEN,
    Console.RED,
    Console.YELLOW
  )

  def build(base: Village): String = {
    buildString(buildCollection(base))
  }

  def buildColored(base: Village): String = {
    build(base).toIterable
      .map(c => colorChar(c) + c.toString)
      .mkString("") + Console.RESET
  }

  private def colorChar(char: Char): String = {
    if (WallChars.contains(char)) return Console.WHITE
    Colors.toVector(Math.abs(char.hashCode) % Colors.size)
  }

  private def buildString(collection: List[List[Char]]): String = {
    collection.map(_ :+ "\n")
      .map(_.mkString(""))
      .mkString("")
  }

  private def buildCollection(base: Village): List[List[Char]] = {
    drawBoundary(
      drawCCRadius(
        base,
        drawElements(base.elements.toList, List.fill[Char](TileCoordinate.Max, TileCoordinate.Max) { ' ' })
      )
    )
  }

  private def drawCCRadius(village: Village, current: List[List[Char]]): List[List[Char]] = {
    val clanCastle = village.clanCastle
    if (clanCastle.isEmpty) return current
    drawCCRadius(
      clanCastle.get.radius,
      current,
      TileCoordinate.AllElementPlacement.toSeq
    )
  }

  private def drawCCRadius(radius: Radius, current: List[List[Char]], coords: Seq[TileCoordinate]): List[List[Char]] = {
    if (coords.isEmpty) return current
    if (Math.abs(coords.head.distanceTo(radius.coordinate) - radius.size.toInt) > 0.5) {
      return drawCCRadius(radius, current, coords.tail)
    }
    drawCCRadius(
      radius,
      draw(current, coords.head, '^'),
      coords.tail
    )
  }

  private def drawElements(elements: List[Element], current: List[List[Char]]): List[List[Char]] = {
    if (elements.isEmpty) return current
    drawElements(elements.tail, drawElement(elements.head, current))
  }

  // TODO: Find functional solution
  private def drawElement(element: Element, current: List[List[Char]]): List[List[Char]] = {
    val coords = element.coordinate
      .matrixOfCoordinatesTo(element.coordinate.offset(element.size.toInt - 1, element.size.toInt - 1))

    val char = characterForElement(element)
    var newList = current
    for (coord <- coords) {
      newList = draw(newList, coord, char)
    }
    newList
  }

  private def draw(map: List[List[Char]], coord: TileCoordinate, char: Char): List[List[Char]] = {
    map.patch(coord.y, Seq(map(coord.y).patch(coord.x, Seq(char), 1)), 1)
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
        val charSeed = element.getClass.getName
        possibles.toVector(Math.abs(charSeed.hashCode) % possibles.size)
    }
  }

  private def drawBoundary(current: List[List[Char]]): List[List[Char]] = {
    (horizontalWall :: verticalWall(current)) :+ horizontalWall
  }

  private def verticalWall(current: List[List[Char]]): List[List[Char]] = {
    if (current.isEmpty) return current
    ((WallVert :: current.head) :+ WallVert) +: verticalWall(current.tail)
  }

  private val horizontalWall: List[Char] = (WallCorner :: List.fill[Char](TileCoordinate.Max) { WallHor }) :+ WallCorner
}
