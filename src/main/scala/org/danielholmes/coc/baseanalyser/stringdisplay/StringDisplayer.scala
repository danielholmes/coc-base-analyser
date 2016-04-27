package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._

class StringDisplayer {
  import StringDisplayer._

  def build(base: Village): String = {
    buildString(buildCollection(base))
  }

  def buildColoured(base: Village): String = {
    build(base).toIterable
      .map(c => colorChar(c) + c.toString)
      .mkString("") + Console.RESET
  }

  private def colorChar(char: Char): String = {
    if (WallChars.contains(char)) return Console.WHITE
    Colors.toVector(Math.abs(char.hashCode) % Colors.size)
  }

  private def buildString(collection: Seq[Seq[Char]]): String = {
    collection.map(_ :+ "\n")
      .map(_.mkString(""))
      .mkString("")
  }

  private def buildCollection(base: Village): Seq[Seq[Char]] = {
    drawBoundary(
      drawCCRadius(
        base,
        drawElements(base.elements.toList, List.fill[Char](Tile.MaxCoordinate + 1, Tile.MaxCoordinate + 1) { ' ' })
      )
    )
  }

  private def drawCCRadius(village: Village, current: List[List[Char]]): List[List[Char]] = {
    village.clanCastle
      .map(_.range)
      .map(drawCCRadius(_, current, Tile.All.toSeq))
      .getOrElse(current)
  }

  private def drawCCRadius(range: ElementRange, current: List[List[Char]], tiles: Seq[Tile]): List[List[Char]] = {
    if (tiles.isEmpty) return current
    if (!range.touchesEdge(tiles.head)) return drawCCRadius(range, current, tiles.tail)
    drawCCRadius(
      range,
      draw(current, tiles.head, '^'),
      tiles.tail
    )
  }

  private def drawElements(elements: List[Element], current: List[List[Char]]): List[List[Char]] = {
    if (elements.isEmpty) return current
    drawElements(elements.tail, drawElement(elements.head, current))
  }

  private def drawElement(element: Element, current: List[List[Char]]): List[List[Char]] = {
    drawElement(
      element,
      element.block.tiles.toSeq,
      current
    )
  }

  private def drawElement(element: Element, tiles: Seq[Tile], current: List[List[Char]]): List[List[Char]] = {
    if (tiles.isEmpty) return current
    drawElement(
      element,
      tiles.tail,
      draw(current, tiles.head, characterForElement(element))
    )
  }

  private def draw(map: List[List[Char]], tile: Tile, char: Char): List[List[Char]] = {
    map.patch(tile.y, Seq(map(tile.y).patch(tile.x, Seq(char), 1)), 1)
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
      case _: HiddenTesla => 'T'
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
    (HorizontalWall :: verticalWall(current)) :+ HorizontalWall
  }

  private def verticalWall(current: List[List[Char]]): List[List[Char]] = {
    if (current.isEmpty) return current
    ((WallVert :: current.head) :+ WallVert) +: verticalWall(current.tail)
  }
}

object StringDisplayer {
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

  private val HorizontalWall: List[Char] = (WallCorner :: List.fill[Char](TileCoordinate.MaxCoordinate) { WallHor }) :+ WallCorner
}
