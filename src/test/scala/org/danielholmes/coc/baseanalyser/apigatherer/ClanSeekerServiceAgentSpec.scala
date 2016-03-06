package org.danielholmes.coc.baseanalyser.apigatherer

import org.scalatest._

class ClanSeekerServiceAgentSpec extends FlatSpec with Matchers {
  val client = new ClanSeekerServiceAgent

  "Clan Seeker Service Client" should "get clan details for OH alpha" in {
    client.getClanDetails(154621406673L).clan.name should be ("OneHive Alpha")
  }

  it should "get player village for Dakota" in {
    client.getPlayerVillage(223343461050L).player.village.raw should not be null
  }
}