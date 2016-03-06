package org.danielholmes.coc.baseanalyser.web

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.servlet.WebBoot

import scala.concurrent.duration._

trait WebApp {
  implicit val system = ActorSystem("WebApp")
  val apiActor = system.actorOf(Props[WebAppServiceActor], "webAppActor")
}

class WebAppServlet extends WebBoot with WebApp {
  override val serviceActor = apiActor
}

object SprayApiCan extends App with WebApp {
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(apiActor, interface = "localhost", port = 8080)
}