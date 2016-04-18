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

class WebAppServiceActor extends Actor with HttpService with Services {
  def actorRefFactory = context

  def receive = runRoute(routes)

  implicit val timeout = Timeout(500.seconds)

  val routes =
    compressResponse() {
      path("") {
        getFromResource("web/index.html")
      } ~
      path("war-bases" / Segment) { (clanCode) =>
        getFromResource("web/war-bases.html")
      } ~
      pathPrefix("assets") {
        getFromResourceDirectory("web/assets")
      } ~
      path("clan-war-bases-analysis" / Segment) { (clanCode) =>
        val start = System.currentTimeMillis
        permittedClans.find(_.code == clanCode)
          .map(clan => clanSeekerServiceAgent.getClanDetails(clan.id))
          .map(details => details.clan.players.map(_.avatar.userId))
          // Can make par here but clan seeker service doesnt seem to be up to it
          .map(userIds => userIds.par.map(userId => clanSeekerServiceAgent.getPlayerVillage(userId)))
          .map(_.filter(_.player.nonEmpty).map(_.player.get))
          .map(_.map(p => villageJsonParser.parse(p.village.raw)))
          .map(_.map(_.war))
          .map(_.filter(_.nonEmpty))
          .map(_.map(_.get))
          .map(_.map(villageAnalyser.analyse))
          .map(_.filter(_.nonEmpty))
          .map(_.map(_.get).seq)
          // TODO: Only need success for rules, and player name and TH level and only need one overall time
          .map(viewModelMapper.viewModel(_, Duration.ofMillis(System.currentTimeMillis - start)))
          .map(complete(_))
          .getOrElse(complete(StatusCodes.NotFound, s""""Clan code $clanCode unknown""""))
      } ~
      path("village-analysis" / Segment / Segment) { (userName, layoutCode) =>
        respondWithMediaType(`application/json`) {
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