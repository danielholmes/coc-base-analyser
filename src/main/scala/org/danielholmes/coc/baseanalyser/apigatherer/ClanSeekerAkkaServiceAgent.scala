package org.danielholmes.coc.baseanalyser.apigatherer

import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ClanSeekerProtocol extends DefaultJsonProtocol {
  case class AvatarSummary(userName: String, userId: Long)
  case class PlayerSummary(avatar: AvatarSummary)
  case class ClanDetails(name: String, players: Set[PlayerSummary])
  case class ClanDetailsResponse(clan: ClanDetails)

  implicit val AvatarSummaryFormat = jsonFormat2(AvatarSummary)
  implicit val PlayerSummaryFormat = jsonFormat1(PlayerSummary)
  implicit val ClanDetailsFormat = jsonFormat2(ClanDetails)
  implicit val ClanDetailsResponseFormat = jsonFormat1(ClanDetailsResponse)

  case class RawVillage(raw: String)
  case class PlayerVillage(avatar: AvatarSummary, village: RawVillage)
  case class PlayerVillageResponse(player: Option[PlayerVillage])

  implicit val VillageFormat = jsonFormat1(RawVillage)
  implicit val PlayerVillageFormat = jsonFormat2(PlayerVillage)
  implicit val PlayerVillageResponseFormat = jsonFormat1(PlayerVillageResponse)
}
import ClanSeekerProtocol._

// Details for berserker. Note that kicks off account though
// ?userId=55845388039&userToken=bp9a6dt8tawn6mbsp69jec8j7cszfd7968df82ka
class ClanSeekerAkkaServiceAgent extends ClanSeekerServiceAgent {
  private val rootUrl: String = "http://api.clanseeker.co"
  private val timeout = 1.minute

  def getClanDetails(id: Long): ClanDetailsResponse = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures
    try {
      val pipeline: HttpRequest => Future[ClanDetailsResponse] = sendReceive ~> unmarshal[ClanDetailsResponse]
      val response: Future[ClanDetailsResponse] = pipeline(Get(s"$rootUrl/clan_details?id=$id"))
      Await.result(response, timeout)
    } finally {
      system.shutdown()
    }
  }

  def getPlayerVillage(id: Long): PlayerVillageResponse = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures
    try {
      val pipeline: HttpRequest => Future[PlayerVillageResponse] = sendReceive ~> unmarshal[PlayerVillageResponse]
      val response: Future[PlayerVillageResponse] = pipeline(Get(s"$rootUrl/player_village?id=$id"))
      Await.result(response, timeout)
    } finally {
      system.shutdown()
    }
  }
}
