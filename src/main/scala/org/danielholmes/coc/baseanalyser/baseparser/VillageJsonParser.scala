package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model.Village
import spray.json._

object VillageJsonProtocol extends DefaultJsonProtocol {
  implicit val buildingFormat = jsonFormat14(RawBuilding)
  implicit val rawVillageFormat = jsonFormat2(RawVillage)
}

import VillageJsonProtocol._

class VillageJsonParser(elementFactory: ElementFactory) {
  def parse(input: String): Villages = {
    try {
      val rawVillage = input.parseJson.convertTo[RawVillage]
      Villages(
        parseVillage(rawVillage, b => RawElement(b.data, b.lvl, b.x, b.y)),
        rawVillage.war_layout
          .map(layoutIndex => parseVillage(rawVillage, b => parseWarElement(b, layoutIndex)))
      )
    } catch {
      case e: JsonParser.ParsingException => throw new InvalidJsonException(e)
      case e: DeserializationException => throw new InvalidJsonException(e)
    }
  }

  private def parseVillage(raw: RawVillage, factory: (RawBuilding) => RawElement): Village = {
    Village(
      raw.buildings
        .map(factory)
        .map(elementFactory.build)
        .filter(_.nonEmpty)
        .map(_.get)
    )
  }

  private def parseWarElement(raw: RawBuilding, index: Int): RawElement = {
    val coords = parseWarCoordinates(raw, index)
    RawElement(raw.data, raw.lvl, coords._1, coords._2)
  }

  private def parseWarCoordinates(raw: RawBuilding, index: Int): (Int, Int) = {
    index match {
      case 1 => (raw.l1x.get, raw.l1y.get)
      case 2 => (raw.l2x.get, raw.l2y.get)
      case 3 => (raw.l3x.get, raw.l3y.get)
      case 4 => (raw.l4x.get, raw.l4y.get)
      case 5 => (raw.l5x.get, raw.l5y.get)
      case _ => throw new RuntimeException(s"Unknown war layout $index")
    }
  }
}

case class RawVillage(buildings: Set[RawBuilding], war_layout: Option[Int])
case class RawBuilding(
  data: Int,
  lvl: Int,
  x: Int, y: Int,
  l1x: Option[Int], l1y: Option[Int],
  l2x: Option[Int], l2y: Option[Int],
  l3x: Option[Int], l3y: Option[Int],
  l4x: Option[Int], l4y: Option[Int],
  l5x: Option[Int], l5y: Option[Int]
)
case class RawElement(data: Int, lvl: Int, x: Int, y: Int)

case class Villages(home: Village, war: Option[Village])

class InvalidJsonException(cause : Throwable) extends Exception(cause)
