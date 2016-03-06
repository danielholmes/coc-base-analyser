package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.apigatherer.{ClanSeekerServiceAgent, VillageGatherer}
import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer

object PrintBase {
  def main(args: Array[String]) = {
    if (args.length != 1) {
      throw new RuntimeException("Must provide userName arg")
    }

    val printer = new StringDisplayer
    val gatherer = new VillageGatherer(
      new ClanSeekerServiceAgent("http://api.clanseeker.co"),
      new VillageJsonParser(new HardCodedElementFactory())
    )

    val village = gatherer.gatherByUserName(args(0))
    println(printer.buildColored(village))
  }
}
