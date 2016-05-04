package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.analysis.{AnalysisReport, VillageAnalyser}
import org.danielholmes.coc.baseanalyser.gameconnection.ClanSeekerProtocol.PlayerVillage
import org.danielholmes.coc.baseanalyser.gameconnection.GameConnection
import org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParser
import org.danielholmes.coc.baseanalyser.model.Layout._
import org.danielholmes.coc.baseanalyser.model.{Layout, Village}
import org.danielholmes.coc.baseanalyser.web.PermittedClan
import org.scalactic.{Bad, Good, Or}

class Facades(
  permittedClans: Set[PermittedClan],
  clanSeeker: GameConnection,
  villageJsonParser: VillageJsonParser,
  villageAnalyser: VillageAnalyser
) {
  def getVillageAnalysis(clanCode: String, playerId: Long, layoutName: String):
    ((PermittedClan, Option[AnalysisReport], Village, PlayerVillage, Layout, Long) Or String) = {
    permittedClans.find(_.code == clanCode)
      .map(clan =>
        Layout.values.find(_.toString == layoutName)
          .map(layout =>
            clanSeeker.getPlayerVillage(playerId)
              .filter(_.avatar.clanId == clan.id)
              .map(player =>
                villageJsonParser.parse(player.village.raw)
                  .getByLayout(layout)
                  .map(village => {
                    val start = System.currentTimeMillis
                    Good((clan, villageAnalyser.analyse(village), village, player, layout, start))
                  })
                  .getOrElse(Bad(s"Player ${player.avatar.userName} doesn't have $layout village"))
              )
              .getOrElse(Bad(s"id $playerId not found in clan ${clan.name}"))
          )
          .getOrElse(Bad(s"Layout type $layoutName unknown"))
      )
      .getOrElse(Bad(s"Clan with code $clanCode not found"))
  }

  def getWarVillageByUserName(clanCode: String, userName: String): Village Or String = {
    permittedClans.find(_.code == clanCode)
      .map(clan =>
        clanSeeker.getClanDetails(clan.id)
          .map(clanDetails =>
            clanDetails.players.find(_.avatar.userName.equalsIgnoreCase(userName))
              .map(_.avatar.currentHomeId)
              .map(userId =>
                clanSeeker.getPlayerVillage(userId)
                  .map(_.village.raw)
                  .map(villageJsonParser.parse)
                  .map(
                    _.war
                      .map(Good(_))
                      .getOrElse(Bad(s"User $userName has no war village"))
                  )
                  .getOrElse(Bad("Error communicating with servers"))
              )
              .getOrElse(Bad(s"Player $userName doesn't exist"))
          )
          .getOrElse(Bad("Error communicating with servers"))
      )
      .getOrElse(Bad(s"Clan code $clanCode not found"))
  }
}
