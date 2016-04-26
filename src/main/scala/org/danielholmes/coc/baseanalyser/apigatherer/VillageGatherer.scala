package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.ClanDetails
import org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParser
import org.danielholmes.coc.baseanalyser.model.Village
import org.danielholmes.coc.baseanalyser.model.Layout.Layout
import org.danielholmes.coc.baseanalyser.web.PermittedClan

@Deprecated // too inefficient
class VillageGatherer(
  private val serviceAgent: ClanSeekerServiceAgent,
  private val villageParser: VillageJsonParser,
  private val permittedClans: Set[PermittedClan]
) {
  def gatherByUserName(userName: String, layout: Layout): Option[Village] = {
    getPlayerIdByUserName(userName)
      .flatMap(serviceAgent.getPlayerVillage)
      .map(_.village)
      .map(_.raw)
      .map(villageParser.parse)
      .flatMap(_.getByLayout(layout))
  }

  private def getPlayerIdByUserName(userName: String): Option[Long] = {
    getPlayerIdByUserNameCaseInsensitive(userName, permittedClans.toSeq)
  }

  private def getPlayerIdByUserNameCaseInsensitive(userName: String, clansToCheck: Seq[PermittedClan]): Option[Long] = {
    if (clansToCheck.isEmpty) return None
    serviceAgent.getClanDetails(clansToCheck.head.id)
      .flatMap(getPlayerId(_, userName))
      .orElse(getPlayerIdByUserNameCaseInsensitive(userName, clansToCheck.tail))
  }

  private def getPlayerId(clan: ClanDetails, userName: String): Option[Long] = {
    clan.players
      .map(_.avatar)
      .find(_.userName.equalsIgnoreCase(userName))
      .map(_.userId)
  }
}
