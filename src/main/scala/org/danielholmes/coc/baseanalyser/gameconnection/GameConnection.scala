package org.danielholmes.coc.baseanalyser.gameconnection

import org.danielholmes.coc.baseanalyser.gameconnection.ClanSeekerProtocol.{ClanDetails, ClanDetailsResponse, PlayerVillage, PlayerVillageResponse}

trait GameConnection {
  def getClanDetails(id: Long): Option[ClanDetails]

  def getPlayerVillage(id: Long): Option[PlayerVillage]
}
