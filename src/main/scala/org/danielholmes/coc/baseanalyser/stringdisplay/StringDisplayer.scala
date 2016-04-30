package org.danielholmes.coc.baseanalyser.stringdisplay

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense._
import org.danielholmes.coc.baseanalyser.model.heroes.{ArcherQueenAltar, BarbarianKingAltar}
import org.danielholmes.coc.baseanalyser.model.range.ElementRange
import org.danielholmes.coc.baseanalyser.model.special.{ClanCastle, TownHall}

import scala.annotation.tailrec

class StringDisplayer {
  import StringDisplayer._

  def build(base: Village): String = buildString(buildCollection(base))

  def buildColoured(base: Village): String = {
    build(base).toIterable
      .map(c => colorChar(c) + c.toString)
      .mkString("") + Console.RESET
  }

  private def colorChar(char: Char): String = {
    if (WallChars.contains(char)) {
      Console.WHITE
    } else {
      Colors.toVector(Math.abs(char.hashCode) % Colors.size)
    }
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
      .map(drawCCRadius(_, current, Tile.All.toList))
      .getOrElse(current)
  }

  @tailrec
  private def drawCCRadius(range: ElementRange, current: List[List[Char]], tiles: List[Tile]): List[List[Char]] = {
    tiles match {
      case Nil => current
      case head :: tail => {
        val newCurrent = if (range.touchesEdge(tiles.head)) { draw(current, head, '^') } else { current }
        drawCCRadius(range, newCurrent, tail)
      }
    }
  }

  @tailrec
  private def drawElements(elements: List[Element], current: List[List[Char]]): List[List[Char]] = {
    elements match {
      case Nil => current
      case head :: tail => drawElements(tail, drawElement(head, current))
    }
  }

  private def drawElement(element: Element, current: List[List[Char]]): List[List[Char]] = {
    drawElement(characterForElement(element), element.block.tiles.toList, current)
  }

  @tailrec
  private def drawElement(char: Char, tiles: List[Tile], current: List[List[Char]]): List[List[Char]] = {
    tiles match {
      case Nil => current
      case head :: tail => drawElement(char, tail, draw(current, head, char))
    }
  }

  private def draw(map: List[List[Char]], tile: Tile, char: Char): List[List[Char]] = {
    map.patch(tile.y, Seq(map(tile.y).patch(tile.x, Seq(char), 1)), 1)
  }

  private def characterForElement(element: Element): Char = {
    element match {
      case _: ArcherTower => 'A'
      case _: BarbarianKingAltar => 'B'
      case _: AirDefense => 'D'
      case _: Cannon => 'C'
      case _: InfernoTower => 'I'
      case _: Mortar => 'M'
      case _: ArcherQueenAltar => 'Q'
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
    (HorizontalWall :: verticalWall(current, List.empty)) :+ HorizontalWall
  }

  @tailrec
  private def verticalWall(inner: List[List[Char]], current: List[List[Char]]): List[List[Char]] = {
    inner match {
      case Nil => current
      case head :: tail => verticalWall(tail, current :+ ((WallVert :: head) :+ WallVert))
    }
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
