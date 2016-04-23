package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.{ClanDetails, ClanDetailsResponse, PlayerVillage, PlayerVillageResponse}

trait ClanSeekerServiceAgent {
  def getClanDetails(id: Long): Option[ClanDetails]

  def getPlayerVillage(id: Long): Option[PlayerVillage]
}
