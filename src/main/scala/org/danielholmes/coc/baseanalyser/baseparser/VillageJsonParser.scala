package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model.Village
import spray.json._

object VillageJsonProtocol extends DefaultJsonProtocol {
  implicit val buildingFormat = jsonFormat4(RawBuilding)
  implicit val rawVillageFormat = jsonFormat1(RawVillage)
}

import VillageJsonProtocol._

class VillageJsonParser(elementFactory: ElementFactory) {
  def parse(input: String): Village = {
    try {
      Village(
        input.parseJson
          .convertTo[RawVillage]
          .buildings
          .map(elementFactory.build)
          .filter(_.nonEmpty)
          .map(_.get)
      )
    } catch {
      case e: JsonParser.ParsingException => throw new InvalidJsonException(e)
      case e: DeserializationException => throw new InvalidJsonException(e)
    }
  }
}

case class RawVillage(buildings: Set[RawBuilding])
case class RawBuilding(data: Int, lvl: Int, x: Int, y: Int)

class InvalidJsonException(cause : Throwable) extends Exception(cause)
