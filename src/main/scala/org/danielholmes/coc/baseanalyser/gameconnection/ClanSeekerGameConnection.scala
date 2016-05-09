package org.danielholmes.coc.baseanalyser.gameconnection

import akka.actor.ActorSystem
import org.danielholmes.coc.baseanalyser.util.GameConnectionNotAvailableException
import org.scalactic.anyvals.PosZInt
import spray.can.Http.ConnectionAttemptFailedException
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.http._
import spray.client.pipelining._
import spray.httpx.unmarshalling.FromResponseUnmarshaller

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.reflect.{ClassTag, classTag}

object ClanSeekerProtocol extends DefaultJsonProtocol {
  case class AvatarSummary(userName: String, currentHomeId: Long, clanId: Long)
  case class PlayerSummary(avatar: AvatarSummary)
  case class ClanDetails(name: String, players: Set[PlayerSummary])
  case class ClanDetailsResponse(clan: Option[ClanDetails])

  implicit val AvatarSummaryFormat = jsonFormat3(AvatarSummary)
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

// TODO: Having problems with types when refactor into common functionality, but should refactor this
class ClanSeekerGameConnection extends GameConnection {
  private val maxAttempts = 3
  private val rootUrl = "http://api.clanseeker.co"
  private val timeout = 1.minute

  def getClanDetails(id: Long): Option[ClanDetails] = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures

    def tryAgain(attemptNumber: Int): Option[ClanDetails] = {
      Thread.sleep(400 * attemptNumber)
      attempt(attemptNumber + 1)
    }

    //@tailrec
    def attempt(attemptNumber: Int): Option[ClanDetails] = {
      val pipeline = sendReceive ~> unmarshal[ClanDetailsResponse]
      try {
        val response = pipeline(Get(s"$rootUrl/clan_details?id=$id"))
        val result = Await.result(response, timeout)
        if (result.clan.isDefined || attemptNumber == maxAttempts) {
          result.clan
        } else {
          tryAgain(attemptNumber)
        }
      } catch {
        case e: ConnectionAttemptFailedException =>
          throw new GameConnectionNotAvailableException()
      }
    }

    try {
      attempt(0)
    } finally {
      system.terminate()
    }
  }

  def getPlayerVillage(id: Long): Option[PlayerVillage] = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures

    def tryAgain(attemptNumber: Int): Option[PlayerVillage] = {
      Thread.sleep(400 * attemptNumber)
      attempt(attemptNumber + 1)
    }

    //@tailrec
    def attempt(attemptNumber: Int): Option[PlayerVillage] = {
      val pipeline = sendReceive ~> unmarshal[PlayerVillageResponse]
      try {
        val response = pipeline(Get(s"$rootUrl/player_village?id=$id"))
        val result = Await.result(response, timeout)
        if (result.player.isDefined || attemptNumber == maxAttempts) {
          result.player
        } else {
          tryAgain(attemptNumber)
        }
      } catch {
        case e: ConnectionAttemptFailedException =>
          throw new GameConnectionNotAvailableException()
      }
    }

    try {
      attempt(0)
    } finally {
      system.terminate()
    }
  }
}
