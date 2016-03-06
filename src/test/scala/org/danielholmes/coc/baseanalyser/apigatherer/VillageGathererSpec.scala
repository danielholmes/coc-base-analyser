package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.Services
import org.scalatest._

class VillageGathererSpec extends FlatSpec with Matchers with Services {
  "Base Gatherer" should "return village for uncool member" in {
    villageGatherer.gatherByUserName("dhau") should not be empty
  }

  it should "return village for Alpha member" in {
    villageGatherer.gatherByUserName("Dakota") should not be empty
  }

  it should "throw not found if no matching user" in {
    villageGatherer.gatherByUserName("Somerandomname would never exist") should be (empty)
  }
}