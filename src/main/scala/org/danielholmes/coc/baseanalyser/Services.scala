package org.danielholmes.coc.baseanalyser

import com.github.mustachejava.DefaultMustacheFactory
import com.twitter.mustache.ScalaObjectHandler
import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.apigatherer.{ClanSeekerAkkaServiceAgent, HardcodedClanSeekerServiceAgent, VillageGatherer}
import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.danielholmes.coc.baseanalyser.stringdisplay.{StringDisplayer, StringTroopDropDisplayer}
import org.danielholmes.coc.baseanalyser.web.{MustacheRenderer, PermittedClan, ViewModelMapper}
import org.scalactic.anyvals.PosInt

trait Services {
  import com.softwaremill.macwire._

  lazy val clanSeekerServiceAgent = wire[ClanSeekerAkkaServiceAgent]
  //lazy val clanSeekerServiceAgent = wire[HardcodedClanSeekerServiceAgent]
  private lazy val elementFactory = wire[HardCodedElementFactory]
  lazy val villageJsonParser = wire[VillageJsonParser]
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
    wire[WizardTowersOutOfHoundPositionsRule],
    wire[QueenWalkedAirDefenseRule],
    wire[QueenWontLeaveCompartmentRule]
  )
  private lazy val th10Rules: Set[Rule] = th9Rules
  private lazy val th11Rules: Set[Rule] = th10Rules
  private lazy val rulesByThLevel = Map(
    PosInt(8) -> th8Rules,
    PosInt(9) -> th9Rules,
    PosInt(10) -> th10Rules,
    PosInt(11) -> th11Rules
  )

  private def mustacheFactory = {
    val mf = new DefaultMustacheFactory()
    mf.setObjectHandler(new ScalaObjectHandler())
    mf
  }

  lazy val mustacheRenderer = wire[MustacheRenderer]

  lazy val permittedClans = Set[PermittedClan](
    PermittedClan("alpha", "OneHive Alpha", 154621406673L),
    PermittedClan("genesis", "OneHive Genesis", 128850679685L),
    PermittedClan("uncool", "Uncool", 103079424453L),
    PermittedClan("aerial", "Aerial Assault", 227634713283L)
  )
  lazy val villageAnalyser = wire[VillageAnalyser]
  lazy val clanWarVillagesAnalyser = wire[ClanWarVillagesAnalyser]
  lazy val stringDisplayer = wire[StringDisplayer]
  lazy val stringTroopDropDisplayer = wire[StringTroopDropDisplayer]
  lazy val viewModelMapper = wire[ViewModelMapper]
}
