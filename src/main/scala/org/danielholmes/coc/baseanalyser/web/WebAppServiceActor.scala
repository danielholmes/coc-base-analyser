package org.danielholmes.coc.baseanalyser.web

import java.io.StringWriter

import akka.actor.Actor
import com.github.mustachejava.DefaultMustacheFactory
import org.danielholmes.coc.baseanalyser.Services
import spray.routing._
import spray.http._
import MediaTypes._
import scala.io.Source
import spray.httpx.SprayJsonSupport._
import ViewModelProtocol._

class WebAppServiceActor extends Actor with HttpService with Services {
  def actorRefFactory = context

  def receive = runRoute(routes)

  val routes =
    compressResponse() {
      path("") {
        get {
          val mf = new DefaultMustacheFactory()
          val mustache = mf.compile("web/home.mustache")
          val writer = new StringWriter
          mustache.execute(writer, Map()).flush()

          respondWithMediaType(`text/html`) {
            complete(writer.toString)
          }
        }
      } ~
      pathPrefix("assets") {
        getFromResourceDirectory("web/assets")
      } ~
      path("village-analysis" / Rest) { userName =>
        respondWithMediaType(`application/json`) {
          val village = villageGatherer.gatherByUserName(userName)
          if (village.isEmpty) {
            complete(StatusCodes.NotFound, s"userName $userName not found in approved clans")
          } else {
            val analysis = villageAnalyser.analyse(village.get)
            if (analysis.isEmpty) {
              complete(StatusCodes.BadRequest, s"$userName village can't be analysed")
            } else {
              complete(viewModelMapper.viewModel(analysis.get))
            }
          }
        }
      }
    }
}