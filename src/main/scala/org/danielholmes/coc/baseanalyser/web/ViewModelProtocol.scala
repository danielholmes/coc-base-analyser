package org.danielholmes.coc.baseanalyser.web

import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

object ViewModelProtocol extends DefaultJsonProtocol {
  implicit object ElementJsonFormat extends RootJsonFormat[ElementViewModel] {
    def write(e: ElementViewModel): JsValue = e match {
      case d: DefenseElementViewModel => defenseElementFormat.write(d)
      case d: HiddenTeslaViewModel => hiddenTeslaFormat.write(d)
      case c: ClanCastleElementViewModel => clanCastleElementFormat.write(c)
      case s: BaseStructureElementViewModel => baseStructureElementFormat.write(s)
      case t: TrapElementViewModel => trapElementFormat.write(t)
    }

    def read(value: JsValue): ElementViewModel = {
      throw new NotImplementedError()
    }
  }

  implicit object RuleResultJsonFormat extends RootJsonFormat[RuleResultViewModel] {
    def write(r: RuleResultViewModel): JsValue = r match {
      case h: HogCCLureResultViewModel => hogCCLureResultFormat.write(h)
      case a: ArcherAnchorResultViewModel => archerAnchorResultFormat.write(a)
      case a: HighHPUnderAirDefResultViewModel => highHPUnderAirDefResultFormat.write(a)
      case a: AirSnipedDefenseResultViewModel => airSnipedDefenseResultFormat.write(a)
      case m: MinimumCompartmentsResultViewModel => minimumCompartmentsResultFormat.write(m)
      case b: BKSwappableResultViewModel => bkSwappableResultFormat.write(b)
      case w: WizardTowersOutOfHoundPositionsResultViewModel => wizardTowersOutOfHoundPositionsResultFormat.write(w)
      case q: QueenWalkedAirDefenseResultViewModel => queenWalkedAirDefenseResultFormat.write(q)
      case q: QueenWontLeaveCompartmentRuleResultViewModel => queenWontLeaveCompartmentResultFormat.write(q)
      case t: EnoughPossibleTrapLocationsRuleResultViewModel => enoughPossibleTrapLocationsResultFormat.write(t)
      case _ => throw new RuntimeException(s"Don't know how to serialise ${r.getClass.getSimpleName}")
    }

    def read(value: JsValue): RuleResultViewModel = {
      throw new NotImplementedError()
    }
  }

  implicit object ElementRangeJsonFormat extends RootJsonFormat[ElementRangeViewModel] {
    def write(e: ElementRangeViewModel): JsValue = e match {
      case c: CircularElementRangeViewModel => circularElementRangeFormat.write(c)
      case w: BlindSpotSectorElementRangeViewModel => blindSpotSectorElementRangeFormat.write(w)
      case _ => throw new RuntimeException(s"Can't render ${e.getClass.getSimpleName}")
    }

    def read(value: JsValue): ElementRangeViewModel = {
      throw new NotImplementedError()
    }
  }

  implicit val circularElementRangeFormat = jsonFormat3(CircularElementRangeViewModel)
  implicit val blindSpotSectorElementRangeFormat = jsonFormat5(BlindSpotSectorElementRangeViewModel)

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val mapCoordinateFormat = jsonFormat2(MapCoordinateViewModel)
  implicit val blockFormat = jsonFormat3(BlockViewModel)
  implicit val tileFormat = jsonFormat2(TileViewModel)
  implicit val wallCompartmentFormat = jsonFormat4(WallCompartmentViewModel)
  implicit val possibleLargeTrapFormat = jsonFormat2(PossibleLargeTrapViewModel)

  implicit val trapElementFormat = jsonFormat4(TrapElementViewModel)
  implicit val baseStructureElementFormat = jsonFormat5(BaseStructureElementViewModel)
  implicit val hiddenTeslaFormat = jsonFormat5(HiddenTeslaViewModel)
  implicit val defenseElementFormat = jsonFormat6(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat6(ClanCastleElementViewModel)

  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val minionAttackPositionFormat = jsonFormat3(MinionAttackPositionViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)
  implicit val archerQueenAttackingFormat = jsonFormat3(ArcherQueenAttackingViewModel)
  implicit val wizardTowerHoundTargetingFormat = jsonFormat3(WizardTowerHoundTargetingViewModel)

  implicit val hogCCLureResultFormat = jsonFormat5(HogCCLureResultViewModel)
  implicit val archerAnchorResultFormat = jsonFormat6(ArcherAnchorResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat6(HighHPUnderAirDefResultViewModel)
  implicit val airSnipedDefenseResultFormat = jsonFormat6(AirSnipedDefenseResultViewModel)
  implicit val minimumCompartmentsResultFormat = jsonFormat6(MinimumCompartmentsResultViewModel)
  implicit val bkSwappableResultFormat = jsonFormat5(BKSwappableResultViewModel)
  implicit val wizardTowersOutOfHoundPositionsResultFormat = jsonFormat6(WizardTowersOutOfHoundPositionsResultViewModel)
  implicit val queenWalkedAirDefenseResultFormat = jsonFormat6(QueenWalkedAirDefenseResultViewModel)
  implicit val queenWontLeaveCompartmentResultFormat = jsonFormat4(QueenWontLeaveCompartmentRuleResultViewModel)
  implicit val enoughPossibleTrapLocationsResultFormat = jsonFormat6(EnoughPossibleTrapLocationsRuleResultViewModel)

  implicit val villageFormat = jsonFormat3(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat2(AnalysisReportViewModel)
  implicit val cantAnalyseVillageFormat = jsonFormat2(CantAnalyseVillageViewModel)
  implicit val baseAnalysisProfilingFormat = jsonFormat3(BaseAnalysisProfilingViewModel)
  implicit val baseAnalysisFormat = jsonFormat8(BaseAnalysisViewModel)

  implicit val resultSummaryFormat = jsonFormat2(RuleResultSummaryViewModel)
  implicit val analysisReportSummaryFormat = jsonFormat4(AnalysisReportSummaryViewModel)

  implicit val exceptionFormat = jsonFormat4(ExceptionViewModel)
}
