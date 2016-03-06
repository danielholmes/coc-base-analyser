package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.scalatest._

class VillageGathererSpec extends FlatSpec with Matchers {
  val gatherer = new VillageGatherer(
    new ClanSeekerServiceAgent("http://api.clanseeker.co"),
    new VillageJsonParser(new HardCodedElementFactory())
  )

  "Base Gatherer" should "return village for uncool member" in {
    gatherer.gatherByUserName("dhau") should not be null
  }

  it should "return village for Alpha member" in {
    gatherer.gatherByUserName("Dakota") should not be null
  }

  it should "throw not found if no matching user" in {
    a [PlayerNotFoundException] should be thrownBy {
      gatherer.gatherByUserName("Somerandomname would never exist")
    }
  }
}