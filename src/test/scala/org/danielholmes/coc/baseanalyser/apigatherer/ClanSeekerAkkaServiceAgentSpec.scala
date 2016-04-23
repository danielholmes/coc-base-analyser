package org.danielholmes.coc.baseanalyser.apigatherer

import org.scalatest._

class ClanSeekerAkkaServiceAgentSpec extends FlatSpec with Matchers {
  val client = new ClanSeekerAkkaServiceAgent

  "Clan Seeker Service Client" should "get clan details for OH alpha" in {
    client.getClanDetails(154621406673L).get.name should be ("OneHive Alpha")
  }

  it should "get player village for Dakota" in {
    client.getPlayerVillage(223343461050L).get.village.raw should not be null
  }

  it should "get empty for non-existent" in {
    client.getPlayerVillage(999999999999999L) should be (empty)
  }
}