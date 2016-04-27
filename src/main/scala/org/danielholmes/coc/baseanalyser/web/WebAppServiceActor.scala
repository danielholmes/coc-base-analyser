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
import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.{ClanDetails, PlayerSummary, PlayerVillage}
import org.danielholmes.coc.baseanalyser.model.Layout.Layout
import org.danielholmes.coc.baseanalyser.model.{Layout, Village}
import org.danielholmes.coc.baseanalyser.util.GameConnectionNotAvailableException
import org.scalactic.{Bad, Good, Or}
import spray.util.LoggingContext

class WebAppServiceActor extends Actor with HttpService with Services {
  def actorRefFactory = context

  def receive = runRoute(route)

  implicit val timeout = Timeout(30.seconds)

  override def timeoutRoute: Route = complete(StatusCodes.InternalServerError, "Took too long")

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

  implicit def rejectionHandler(implicit log: LoggingContext) =
    RejectionHandler {
      case Nil => notFoundPage("Page doesn't exist")
    }

  private def generatePlayerAnalysisUrl(clanCode: String, player: PlayerSummary, layout: Layout) = {
    val escape = (part: String) => UrlEscapers.urlPathSegmentEscaper().escape(part)
    s"/clans/${escape(clanCode)}/players/${escape(player.avatar.currentHomeId.toString)}/${layout.toString}"
  }

  private def generatePlayerAnalysisSummaryUrl(clanCode: String, player: PlayerSummary, layout: Layout) = {
    val escape = (part: String) => UrlEscapers.urlPathSegmentEscaper().escape(part)
    s"/clans/${escape(clanCode)}/players/${escape(player.avatar.currentHomeId.toString)}/${layout.toString}/summary"
  }

  private def notFoundPage(message: String) = {
    respondWithMediaType(`text/html`) {
      complete(
        StatusCodes.NotFound,
        mustacheRenderer.render("web/error.mustache", Map("title" -> "Not Found", "message" -> message))
      )
    }
  }

  private def getClanByCode(code: String)(handler: (ClanDetails) => Route): Route = {
    get {
      permittedClans.find(_.code == code)
        .map(clan =>
          clanSeekerServiceAgent.getClanDetails(clan.id)
            .getOrElse(throw new GameConnectionNotAvailableException)
        )
        .map(handler)
        .getOrElse(notFoundPage(s"Clan with code $code not found"))
    }
  }

  val route: Route =
    handleExceptions(exceptionHandler) {
      handleRejections(rejectionHandler) {
        compressResponse() {
          respondWithMediaType(`text/html`) {
            pathSingleSlash {
              getFromResource("web/index.html")
            } ~
            path("clans" / Segment) { (clanCode) =>
              getClanByCode(clanCode) {
                (clanDetails) =>
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
              }
            } ~
            path("clans" / Segment / "war-bases") { clanCode =>
              getClanByCode(clanCode) {
                (clanDetails) =>
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
              }
            } ~
            path("clans" / Segment / "players" / LongNumber / Segment) { (clanCode, playerId, layoutName) =>
              get {
                facades.getVillageAnalysis(clanCode, playerId, layoutName)
                  .map({
                    case (clan, report, village, player, layout, start) =>
                      report.map(analysis =>
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
                  })
                  .recover(notFoundPage)
                  .get
              }
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
              get {
                facades.getVillageAnalysis(clanCode, playerId, layoutName)
                  .map({
                    case (clan, report, village, player, layout, start) =>
                      report
                        .map(analysis =>
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
                  })
                  .recover(message => complete(StatusCodes.NotFound, message.toJson.compactPrint))
                  .get
              }
            }
          }
        }
      }
    }
}