package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParser

class VillageGatherer(private val serviceAgent: ClanSeekerServiceAgent, private val villageParser: VillageJsonParser) {
  private val PermittedClanIds = Set(154621406673L, 128850679685L, 103079424453L) // OH Alpha, OH Genesis, uncool

  def gatherByUserName(userName: String) = {
    val playerId = getPlayerIdByUserName(userName)
      .getOrElse(throw new PlayerNotFoundException(userName))

    val villageJson = serviceAgent.getPlayerVillage(playerId)
      .player
      .village
      .raw
    villageParser.parse(villageJson)
  }

  private def getPlayerIdByUserName(userName: String): Option[Long] = {
    getPlayerIdByUserNameCaseInsensitive(userName, PermittedClanIds.toSeq)
  }

  private def getPlayerIdByUserNameCaseInsensitive(userName: String, clanIds: Seq[Long]): Option[Long] = {
    if (clanIds.isEmpty) return None
    serviceAgent.getClanDetails(clanIds.head)
      .clan
      .players
      .map(_.avatar)
      .find(_.userName == userName)
      .map(_.userId)
      .orElse(getPlayerIdByUserNameCaseInsensitive(userName, clanIds.tail))
  }
}

class PlayerNotFoundException(userName: String) extends Exception(s"Player with userName $userName not found")
