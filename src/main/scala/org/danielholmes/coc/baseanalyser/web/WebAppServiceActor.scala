package org.danielholmes.coc.baseanalyser.web

import java.time.Duration

import akka.actor.Actor
import org.danielholmes.coc.baseanalyser.Services
import spray.routing._
import spray.http._
import MediaTypes._
import akka.util.Timeout

import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport._
import spray.json._
import ViewModelProtocol._
import com.google.common.net.UrlEscapers
import org.danielholmes.coc.baseanalyser.analysis.AnalysisReport
import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.{PlayerSummary, PlayerVillage}
import org.danielholmes.coc.baseanalyser.model.{Layout, Village}
import org.danielholmes.coc.baseanalyser.util.GameConnectionNotAvailableException
import spray.util.LoggingContext

class WebAppServiceActor extends Actor with HttpService with Services {
  def actorRefFactory = context

  def receive = runRoute(route)

  implicit val timeout = Timeout(30.seconds)

  override def timeoutRoute: Route = complete(
    StatusCodes.InternalServerError,
    "Took too long"
  )

  implicit def exceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
        case g: GameConnectionNotAvailableException =>
          respondWithMediaType(`application/json`) {
            log.error(g, g.getMessage)
            complete(
              StatusCodes.ServiceUnavailable,
              Map(
                "message" -> "Connection to Game Servers not available",
                "details" -> "We're currently using a third party service for this which can be unreliable. It's usually only temporary though and worth trying again shortly"
              )
            )
          }
        case e: Exception =>
          respondWithMediaType(`application/json`) {
            requestUri { uri =>
              log.error(e, uri.toString + " - " + e.getMessage)
              complete(StatusCodes.InternalServerError, viewModelMapper.exception(uri, e))
            }
          }
    }

  private def generatePlayerAnalysisUrl(clanCode: String, player: PlayerSummary) = {
    s"/clans/${UrlEscapers.urlPathSegmentEscaper().escape(clanCode)}/players/${UrlEscapers.urlPathSegmentEscaper().escape(player.avatar.currentHomeId.toString)}/war"
  }

  private def getVillageAnalysis(
      clanCode: String,
      playerId: Long,
      layoutCode: String,
      handler: (PermittedClan, Option[AnalysisReport], Village, PlayerVillage, Long) => StandardRoute
  ) = {
    get {
      permittedClans.find(_.code == clanCode)
        .map(clan =>
          Layout.getByCode(layoutCode)
            .map(layout =>
              clanSeekerServiceAgent.getPlayerVillage(playerId)
                .filter(_.avatar.clanId == clan.id)
                .map(player =>
                  villageJsonParser.parse(player.village.raw)
                    .getByLayout(layout)
                    .map(village => {
                      val start = System.currentTimeMillis
                      handler(
                        clan,
                        villageAnalyser.analyse(village),
                        village,
                        player,
                        start
                      )
                    })
                    .getOrElse(complete(StatusCodes.NotFound, s"id ${player.avatar.userName} doesn't have $layout village"))
                )
                .getOrElse(complete(StatusCodes.NotFound, s"id $playerId not found in clan ${clan.name}"))
            )
            .getOrElse(complete(StatusCodes.NotFound, s"Layout type $layoutCode unknown"))
        )
        .getOrElse(complete(StatusCodes.NotFound, s"Clan with code $clanCode not found"))
    }
  }

  val route: Route =
    handleExceptions(exceptionHandler) {
      compressResponse() {
        respondWithMediaType(`text/html`) {
          pathSingleSlash {
            getFromResource("web/index.html")
          } ~
          path("clans" / Segment) { (clanCode) =>
            get {
              permittedClans.find(_.code == clanCode)
                .map(clan =>
                  clanSeekerServiceAgent.getClanDetails(clan.id)
                    .getOrElse(throw new GameConnectionNotAvailableException)
                )
                .map(clanDetails =>
                  complete(
                    mustacheRenderer.render(
                      "web/clan.mustache",
                      Map(
                        "name" -> clanDetails.name,
                        "bulkAnalysisUrl" -> s"/clans/$clanCode/war-bases",
                        "players" -> clanDetails.players
                          .toSeq
                          .sortBy(_.avatar.userName.toLowerCase)
                          .map(p => Map(
                            "ign" -> p.avatar.userName,
                            "analysisUrl" -> generatePlayerAnalysisUrl(clanCode, p)
                          ))
                      )
                    )
                  )
                )
                .getOrElse(
                  complete(StatusCodes.NotFound, s"Clan with code $clanCode not found")
                )
            }
          } ~
          path("clans" / Segment / "war-bases") { clanCode =>
            get {
              permittedClans.find(_.code == clanCode)
                .map(clan =>
                  clanSeekerServiceAgent.getClanDetails(clan.id)
                    .getOrElse(throw new GameConnectionNotAvailableException)
                )
                .map(clanDetails =>
                  complete(
                    mustacheRenderer.render(
                      "web/war-bases.mustache",
                      Map(
                        "name" -> clanDetails.name,
                        "code" -> clanCode,
                        "players" -> clanDetails.players
                          .map(p => Map(
                            "id" -> p.avatar.currentHomeId,
                            "ign" -> p.avatar.userName,
                            "analysisUrl" -> generatePlayerAnalysisUrl(clanCode, p)
                          ))
                      )
                    )
                  )
                )
                .getOrElse(
                  complete(StatusCodes.NotFound, s"Clan with code $clanCode not found")
                )
            }
          } ~
          path("clans" / Segment / "players" / LongNumber / Segment) { (clanCode, playerId, layoutCode) =>
            getVillageAnalysis(
              clanCode,
              playerId,
              layoutCode,
              (clan, possibleAnalysis, village, player, start) =>
                possibleAnalysis.map(analysis =>
                  complete(
                    mustacheRenderer.render(
                      "web/base-analysis.mustache",
                      Map(
                        "clanName" -> clan.name,
                        "playerIgn" -> player.avatar.userName,
                        "report" -> viewModelMapper.analysisReport(
                          analysis,
                          Duration.ofMillis(System.currentTimeMillis - start)
                        ).toJson.compactPrint
                      )
                    )
                  )
                )
                .getOrElse(
                  complete(
                    mustacheRenderer.render(
                      "web/base-analysis.mustache",
                      Map(
                        "clanName" -> clan.name,
                        "playerIgn" -> player.avatar.userName,
                        "warning" -> s"${player.avatar.userName} village can't be analysed - currently only supporting TH${villageAnalyser.minTownHallLevel.toInt}-${villageAnalyser.maxTownHallLevel.toInt}",
                        "report" -> viewModelMapper.analysisReport(
                          AnalysisReport(village, Set.empty),
                          Duration.ofMillis(System.currentTimeMillis - start)
                        ).toJson.compactPrint
                      )
                    )
                  )
                )
            )
          }
        } ~
        pathPrefix("assets") {
          getFromResourceDirectory("web/assets")
        } ~
        path("sys" / "exception") {
          get {
            complete {
              println("Exception on purpose")
              throw new RuntimeException("Some exception happened")
            }
          }
        } ~
        path("sys" / "timeout") { ctx =>
          println("Provoking Timeout")
          // we simply let the request drop to provoke a timeout
        } ~
        respondWithMediaType(`application/json`) {
          path("clans" / Segment / "players" / LongNumber / Segment / "summary") { (clanCode, playerId, layoutCode) =>
            getVillageAnalysis(
              clanCode,
              playerId,
              layoutCode,
              (clan, possibleAnalysis, village, player, start) =>
                possibleAnalysis.map(analysis =>
                  complete(viewModelMapper.analysisSummary(
                    player.avatar.userName,
                    analysis,
                    Duration.ofMillis(System.currentTimeMillis - start)
                  ))
                )
                .getOrElse(
                  complete(
                    StatusCodes.BadRequest,
                    viewModelMapper.cantAnalyseVillage(
                      village,
                      s"${player.avatar.userName} village can't be analysed - currently only supporting TH${villageAnalyser.minTownHallLevel.toInt}-${villageAnalyser.maxTownHallLevel.toInt}"
                    )
                  )
                )
            )
          }
        }
      }
    }
}