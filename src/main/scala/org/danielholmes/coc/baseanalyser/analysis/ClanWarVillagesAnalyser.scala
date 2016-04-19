package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.PlayerSummary
import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerServiceAgent
import org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParser

class ClanWarVillagesAnalyser(
  private val clanSeekerServiceAgent: ClanSeekerServiceAgent,
  private val villageJsonParser: VillageJsonParser,
  private val villageAnalyser: VillageAnalyser
) {
  def analyse(id: Long): Option[ClanAnalysisReport] = {
    Some(clanSeekerServiceAgent.getClanDetails(id))
      .map(_.clan.players)
      .map(analysePlayers)
      .map(ClanAnalysisReport)
  }

  private def analysePlayers(players: Set[PlayerSummary]): Set[PlayerAnalysisReport] = {
    players.map(_.avatar.userId)
      .par
      .map(analysePlayer)
      .seq
  }

  private def analysePlayer(playerId: Long): PlayerAnalysisReport = {
    // Since player id came from clan details api, going to assume it exists
    val player = clanSeekerServiceAgent.getPlayerVillage(playerId).player
        .getOrElse(throw new RuntimeException(s"No player with id $playerId"))
    val villages = villageJsonParser.parse(player.village.raw)
    PlayerAnalysisReport(
      player.avatar.userName,
      villages.home.townHallLevel.get,
      villages.war.flatMap(villageAnalyser.analyse)
    )
  }
}
