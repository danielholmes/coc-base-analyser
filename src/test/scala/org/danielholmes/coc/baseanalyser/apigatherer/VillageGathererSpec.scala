package org.danielholmes.coc.baseanalyser.apigatherer

import org.danielholmes.coc.baseanalyser.Services
import org.danielholmes.coc.baseanalyser.model.Layout
import org.scalatest._

class VillageGathererSpec extends FlatSpec with Matchers with Services {
  "Base Gatherer" should "return village for Aerial Assault member" in {
    villageGatherer.gatherByUserName("dhau", Layout.Home) should not be empty
  }

  it should "return village for Alpha/Genesis member with spaces and weird characters" in {
    villageGatherer.gatherByUserName("I AM SPARTA!!1!", Layout.War) should not be empty
  }

  it should "throw not found if no matching user" in {
    villageGatherer.gatherByUserName("Somerandomname would never exist", Layout.Home) should be (empty)
  }
}