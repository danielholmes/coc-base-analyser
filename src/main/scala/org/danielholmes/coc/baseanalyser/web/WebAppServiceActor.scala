package org.danielholmes.coc.baseanalyser.web

import akka.actor.Actor
import org.danielholmes.coc.baseanalyser.Services
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport._
import ViewModelProtocol._

class WebAppServiceActor extends Actor with HttpService with Services {
  def actorRefFactory = context

  def receive = runRoute(routes)

  val routes =
    compressResponse() {
      path("") {
        getFromResource("web/index.html")
      } ~
      pathPrefix("assets") {
        getFromResourceDirectory("web/assets")
      } ~
      path("village-analysis" / Segment) { userName =>
        respondWithMediaType(`application/json`) {
          val village = villageGatherer.gatherByUserName(userName)
          if (village.isEmpty) {
            complete(StatusCodes.NotFound, s""""IGN $userName not found in approved clans"""")
          } else {
            val analysis = villageAnalyser.analyse(village.get)
            if (analysis.isEmpty) {
              complete(StatusCodes.BadRequest, s""""$userName village can't be analysed - currently only supporting TH8-11"""")
            } else {
              complete(viewModelMapper.viewModel(analysis.get))
            }
          }
        }
      }
    }
}