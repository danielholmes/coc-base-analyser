package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.apigatherer.{ClanSeekerAkkaServiceAgent, HardcodedClanSeekerServiceAgent, VillageGatherer}
import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.danielholmes.coc.baseanalyser.stringdisplay.{StringDisplayer, StringTroopDropDisplayer}
import org.danielholmes.coc.baseanalyser.web.ViewModelMapper
import org.scalactic.anyvals.PosInt

trait Services {
  import com.softwaremill.macwire._

  //private lazy val clanSeekerServiceAgent = wire[ClanSeekerAkkaServiceAgent]
  private lazy val clanSeekerServiceAgent = wire[HardcodedClanSeekerServiceAgent]
  private lazy val elementFactory = wire[HardCodedElementFactory]
  private lazy val villageJsonParser = wire[VillageJsonParser]
  private lazy val th8Rules: Set[Rule] = Set(
    wire[HogCCLureRule],
    wire[HighHPUnderAirDefRule],
    wire[ArcherAnchorRule],
    wire[AirSnipedDefenseRule],
    wire[MinimumCompartmentsRule],
    wire[BKSwappableRule]
  )
  private lazy val th9Rules: Set[Rule] = Set(
    wire[HogCCLureRule],
    wire[AirSnipedDefenseRule],
    wire[BKSwappableRule],
    wire[WizardTowersOutOfHoundPositionsRule]
  )
  private lazy val th10Rules: Set[Rule] = th9Rules
  private lazy val th11Rules: Set[Rule] = th10Rules
  private lazy val rulesByThLevel = Map(
    PosInt(8) -> th8Rules,
    PosInt(9) -> th9Rules,
    PosInt(10) -> th10Rules,
    PosInt(11) -> th11Rules
  )

  lazy val villageAnalyser = wire[VillageAnalyser]
  lazy val villageGatherer = wire[VillageGatherer]
  lazy val stringDisplayer = wire[StringDisplayer]
  lazy val stringTroopDropDisplayer = wire[StringTroopDropDisplayer]
  lazy val viewModelMapper = wire[ViewModelMapper]
}
