package org.danielholmes.coc.baseanalyser.gameconnection

import org.scalatest._

// Specs ignored at the moment because clan seeker not running
class ClanSeekerAkkaServiceAgentSpec extends FlatSpec with Matchers {
  val client = new ClanSeekerGameConnection

  /* "Clan Seeker Service Client" */ ignore should "get clan details for OH alpha" in {
    client.getClanDetails(154621406673L).get.name should be ("OneHive Alpha")
  }

  ignore should "get player village for Dakota" in {
    client.getPlayerVillage(223343461050L).get.village.raw.isEmpty should be (false)
  }

  ignore should "get empty for non-existent" in {
    client.getPlayerVillage(999999999999999L) should be (empty)
  }
}
