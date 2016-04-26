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
import ViewModelProtocol._
import org.danielholmes.coc.baseanalyser.model.Layout
import org.danielholmes.coc.baseanalyser.util.{GameConnectionNotAvailableException, UrlUtils}
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

  case class Basic(name: String)

  val route: Route =
    handleExceptions(exceptionHandler) {
      compressResponse() {
        respondWithMediaType(`text/html`) {
          pathSingleSlash {
            getFromResource("web/index.html")
          } ~
          path("clans" / Segment) { (clanCode) =>
            permittedClans.find(_.code == clanCode)
              .map(clan =>
                clanSeekerServiceAgent.getClanDetails(clan.id)
                  .getOrElse(throw new GameConnectionNotAvailableException)
              )
              .map(clanDetails =>
                get {
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
                            "analysisUrl" -> s"/#${UrlUtils.encodeFragment(p.avatar.userName)}/war"
                          ))
                      )
                    )
                  )
                }
              )
              .getOrElse(
                complete(StatusCodes.NotFound, s"Clan with code $clanCode not found")
              )
          } ~
          path("war-bases" / Segment) { clanCode =>
            // TODO: Remove eventually
            redirect(s"/clans/$clanCode", StatusCodes.MovedPermanently)
          } ~
          path("clans" / Segment / "war-bases") { clanCode =>
            permittedClans.find(_.code == clanCode)
              .map(clan =>
                clanSeekerServiceAgent.getClanDetails(clan.id)
                  .getOrElse(throw new GameConnectionNotAvailableException)
              )
              .map(clanDetails =>
                get {
                  complete(
                    mustacheRenderer.render(
                      "web/war-bases.mustache",
                      Map(
                        "name" -> clanDetails.name,
                        "players" -> clanDetails.players
                          .map(p => Map(
                            "id" -> p.avatar.userId,
                            "ign" -> p.avatar.userName,
                            "analysisUrl" -> s"/#${UrlUtils.encodeFragment(p.avatar.userName)}/war"
                          ))
                      )
                    )
                  )
                }
              )
              .getOrElse(
                complete(StatusCodes.NotFound, s"Clan with code $clanCode not found")
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
          // Note: bypasses approved clan checks
          path("village-analysis" / LongNumber / "war" / "summary") { (playerId) =>
            clanSeekerServiceAgent.getPlayerVillage(playerId)
              .map(player => {
                villageJsonParser.parse(player.village.raw)
                  .war
                  .map(village => {
                    // Can't find easy/flexible way to find memory usage
                    val start = System.currentTimeMillis
                    villageAnalyser.analyse(village)
                      .map(analysis => {
                        complete(viewModelMapper.analysisSummary(
                          player.avatar.userName,
                          analysis,
                          Duration.ofMillis(System.currentTimeMillis - start)
                        ))
                      })
                      .getOrElse(complete(
                        StatusCodes.BadRequest,
                        s"Currently only supporting TH${villageAnalyser.minTownHallLevel.toInt}-${villageAnalyser.maxTownHallLevel.toInt}"
                      ))
                  })
                  .getOrElse(complete(StatusCodes.NotFound, s""""Player has no war base""""))
              })
              .getOrElse(complete(StatusCodes.NotFound, s""""Player not found""""))
          } ~
          path("village-analysis" / Segment / Segment) { (userName, layoutCode) =>
            val layout = Layout.getByCode(layoutCode)
            if (layout.isEmpty) {
              complete(StatusCodes.NotFound, s""""Layout type $layoutCode unknown"""")
            } else {
              val village = villageGatherer.gatherByUserName(userName, layout.get)
              if (village.isEmpty) {
                complete(StatusCodes.NotFound, s""""IGN $userName not found in approved clans"""")
              } else {
                // Can't find easy/flexible way to find memory usage
                val start = System.currentTimeMillis
                val analysis = villageAnalyser.analyse(village.get)
                val end = System.currentTimeMillis
                if (analysis.isEmpty) {
                  complete(StatusCodes.BadRequest, viewModelMapper.viewModel(village.get, s"$userName village can't be analysed - currently only supporting TH8-11"))
                } else {
                  complete(
                    viewModelMapper.viewModel(
                      analysis.get,
                      Duration.ofMillis(end - start)
                    )
                  )
                }
              }
            }
          }
        }
      }
    }
}