package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParser
import org.danielholmes.coc.baseanalyser.model.Village
import org.danielholmes.coc.baseanalyser.model.Layout.Layout
import org.danielholmes.coc.baseanalyser.web.PermittedClan

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
      .getOrElse(throw new RuntimeException(s"Clan ${clansToCheck.head.name} not found in API"))
      .players
      .map(_.avatar)
      .find(_.userName.equalsIgnoreCase(userName))
      .map(_.userId)
      .orElse(getPlayerIdByUserNameCaseInsensitive(userName, clansToCheck.tail))
  }
}
