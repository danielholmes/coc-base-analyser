package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.analysis.{ArcherAnchorRule, HighHPUnderAirDefRule, HogCCLureRule, Rule, VillageAnalyser}
import org.danielholmes.coc.baseanalyser.apigatherer.{HardcodedClanSeekerServiceAgent, VillageGatherer, ClanSeekerAkkaServiceAgent}
import org.danielholmes.coc.baseanalyser.baseparser.{VillageJsonParser, HardCodedElementFactory}
import org.danielholmes.coc.baseanalyser.stringdisplay.{StringTroopDropDisplayer, StringDisplayer}
import org.danielholmes.coc.baseanalyser.web.ViewModelMapper

trait Services {
  import com.softwaremill.macwire._

  private lazy val clanSeekerServiceAgent = wire[ClanSeekerAkkaServiceAgent]
  //private lazy val clanSeekerServiceAgent = wire[HardcodedClanSeekerServiceAgent]
  private lazy val elementFactory = wire[HardCodedElementFactory]
  private lazy val villageJsonParser = wire[VillageJsonParser]
  private lazy val th8Rules: Set[Rule] = Set(wire[HogCCLureRule], wire[HighHPUnderAirDefRule], wire[ArcherAnchorRule])
  private lazy val th9Rules: Set[Rule] = Set(wire[HogCCLureRule])
  private lazy val th10Rules: Set[Rule] = Set(wire[HogCCLureRule])
  private lazy val th11Rules: Set[Rule] = Set(wire[HogCCLureRule])
  private lazy val rulesByThLevel = Map(8 -> th8Rules, 9 -> th9Rules, 10 -> th10Rules, 11 -> th11Rules)

  lazy val villageAnalyser = wire[VillageAnalyser]
  lazy val villageGatherer = wire[VillageGatherer]
  lazy val stringDisplayer = wire[StringDisplayer]
  lazy val stringTroopDropDisplayer = wire[StringTroopDropDisplayer]
  lazy val viewModelMapper = wire[ViewModelMapper]
}
