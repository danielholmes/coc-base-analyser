package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.apigatherer.{ClanSeekerServiceAgent, VillageGatherer}
import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer

object PrintBase {
  def main(args: Array[String]): Int = {
    if (args.length != 1) {
      throw new RuntimeException("Must provide userName arg")
    }

    val printer = new StringDisplayer
    val gatherer = new VillageGatherer(
      new ClanSeekerServiceAgent("http://api.clanseeker.co"),
      new VillageJsonParser(new HardCodedElementFactory())
    )

    val userName = args(0)
    val village = gatherer.gatherByUserName(userName)
    if (village.isEmpty) {
      println(s"userName $userName not found")
      return -1
    }

    println(printer.buildColored(village.get))
    return 0
  }
}
