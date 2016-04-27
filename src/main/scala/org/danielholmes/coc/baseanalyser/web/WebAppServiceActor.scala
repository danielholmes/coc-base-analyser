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
import org.danielholmes.coc.baseanalyser.model.Layout.Layout
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

  private def generatePlayerAnalysisUrl(clanCode: String, player: PlayerSummary, layout: Layout) = {
    val escape = (part: String) => UrlEscapers.urlPathSegmentEscaper().escape(part)
    s"/clans/${escape(clanCode)}/players/${escape(player.avatar.currentHomeId.toString)}/${layout.toString}"
  }

  private def generatePlayerAnalysisSummaryUrl(clanCode: String, player: PlayerSummary, layout: Layout) = {
    val escape = (part: String) => UrlEscapers.urlPathSegmentEscaper().escape(part)
    s"/clans/${escape(clanCode)}/players/${escape(player.avatar.currentHomeId.toString)}/${layout.toString}/summary"
  }

  private def getVillageAnalysis(
      clanCode: String,
      playerId: Long,
      layoutName: String,
      handler: (PermittedClan, Option[AnalysisReport], Village, PlayerVillage, Layout, Long) => StandardRoute
  ) = {
    get {
      permittedClans.find(_.code == clanCode)
        .map(clan =>
          Layout.values.find(_.toString == layoutName)
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
                        layout,
                        start
                      )
                    })
                    .getOrElse(complete(StatusCodes.NotFound, s"Player ${player.avatar.userName} doesn't have $layout village"))
                )
                .getOrElse(complete(StatusCodes.NotFound, s"id $playerId not found in clan ${clan.name}"))
            )
            .getOrElse(complete(StatusCodes.NotFound, s"Layout type $layoutName unknown"))
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
                            "warAnalysisUrl" -> generatePlayerAnalysisUrl(clanCode, p, Layout.War),
                            "homeAnalysisUrl" -> generatePlayerAnalysisUrl(clanCode, p, Layout.Home)
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
                        "players" -> clanDetails.players
                          .map(p => Map(
                            "id" -> p.avatar.currentHomeId,
                            "ign" -> p.avatar.userName,
                            "analysisUrl" -> generatePlayerAnalysisUrl(clanCode, p, Layout.War),
                            "analysisSummaryUrl" -> generatePlayerAnalysisSummaryUrl(clanCode, p, Layout.War)
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
          path("clans" / Segment / "players" / LongNumber / Segment) { (clanCode, playerId, layoutName) =>
            getVillageAnalysis(
              clanCode,
              playerId,
              layoutName,
              (clan, possibleAnalysis, village, player, layout, start) =>
                possibleAnalysis.map(analysis =>
                  complete(
                    mustacheRenderer.render(
                      "web/base-analysis.mustache",
                      Map(
                        "clanName" -> clan.name,
                        "playerIgn" -> player.avatar.userName,
                        "layoutDescription" -> Layout.getDescription(layout),
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
          path("clans" / Segment / "players" / LongNumber / Segment / "summary") { (clanCode, playerId, layoutName) =>
            getVillageAnalysis(
              clanCode,
              playerId,
              layoutName,
              (clan, possibleAnalysis, village, player, layout, start) =>
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
                    s"${player.avatar.userName} village can't be analysed - currently only supporting TH${villageAnalyser.minTownHallLevel.toInt}-${villageAnalyser.maxTownHallLevel.toInt}"
                  )
                )
            )
          }
        }
      }
    }
}