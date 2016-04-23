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
      case e: Exception =>
        respondWithMediaType(`application/json`) {
          requestUri { uri =>
            log.error(e, e.getMessage)
            complete(StatusCodes.InternalServerError, viewModelMapper.viewModel(uri, e))
          }
        }
    }

  val route: Route =
    handleExceptions(exceptionHandler) {
      compressResponse() {
        pathSingleSlash {
          getFromResource("web/index.html")
        } ~
        path("war-bases" / Segment) { (clanCode) =>
          if (permittedClans.exists(_.code == clanCode)) {
            getFromResource("web/war-bases.html")
          } else {
            complete(StatusCodes.NotFound, s""""Clan $clanCode not found"""")
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
          path("clan-war-bases-analysis" / Segment) { (clanCode) =>
            val clan = permittedClans.find(_.code == clanCode)
            if (clan.isEmpty) {
              complete(StatusCodes.NotFound, s""""Clan $clanCode not found"""")
            } else {
              val start = System.currentTimeMillis
              val report = clan.map(_.id).flatMap(clanWarVillagesAnalyser.analyse).get
              complete(viewModelMapper.viewModel(report, Duration.ofMillis(System.currentTimeMillis - start)))
            }
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