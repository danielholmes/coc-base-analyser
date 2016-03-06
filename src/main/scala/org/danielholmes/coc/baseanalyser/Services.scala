package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.analysis.{HighHPUnderAirDefRule, HogCCLureRule, VillageAnalyser}
import org.danielholmes.coc.baseanalyser.apigatherer.{VillageGatherer, ClanSeekerServiceAgent}
import org.danielholmes.coc.baseanalyser.baseparser.{VillageJsonParser, HardCodedElementFactory}
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer
import org.danielholmes.coc.baseanalyser.web.ViewModelMapper

trait Services {
  import com.softwaremill.macwire._

  private lazy val clanSeekerServiceAgent = wire[ClanSeekerServiceAgent]
  private lazy val elementFactory = wire[HardCodedElementFactory]
  private lazy val villageJsonParser = wire[VillageJsonParser]
  private lazy val rules = Set(wire[HogCCLureRule], wire[HighHPUnderAirDefRule])

  lazy val villageAnalyser = wire[VillageAnalyser]
  lazy val villageGatherer = wire[VillageGatherer]
  lazy val stringDisplayer = wire[StringDisplayer]
  lazy val viewModelMapper = wire[ViewModelMapper]
}
