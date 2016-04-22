package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.apigatherer.ClanSeekerProtocol.{ClanDetailsResponse, PlayerVillageResponse}

trait ClanSeekerServiceAgent {
  def getClanDetails(id: Long): ClanDetailsResponse

  def getPlayerVillage(id: Long): PlayerVillageResponse

  def getPlayerVillageWithRetries(id: Long, retries: Int): PlayerVillageResponse
}
