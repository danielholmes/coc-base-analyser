package org.danielholmes.coc.baseanalyser.web

import java.time.Duration
import java.util.{Base64, UUID}

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.HiddenTesla
import org.danielholmes.coc.baseanalyser.model.heroes.HeroAltar
import org.danielholmes.coc.baseanalyser.model.range.{BlindSpotCircularElementRange, CircularElementRange, ElementRange, WedgeElementRange}
import org.danielholmes.coc.baseanalyser.model.special.ClanCastle
import org.danielholmes.coc.baseanalyser.model.troops._
import spray.http.Uri
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

case class TileCoordinateViewModel(x: Int, y: Int)
case class MapCoordinateViewModel(x: Double, y: Double)

sealed trait ElementRangeViewModel {
  val typeName: String
}
case class CircularElementRangeViewModel(inner: Double, outer: Double, override val typeName: String = "Circular") extends ElementRangeViewModel
case class WedgeElementRangeViewModel(angle: Double, size: Double, override val typeName: String = "Wedge") extends ElementRangeViewModel

case class BlockViewModel(x: Int, y: Int, size: Int)
case class TileViewModel(x: Int, y: Int)

sealed trait ElementViewModel {
  def id: String
  def typeName: String
  def level: Int
  def block: BlockViewModel
}
case class BaseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel
) extends ElementViewModel
case class BaseStructureElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  noTroopDropBlock: BlockViewModel
) extends ElementViewModel
case class DefenseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  noTroopDropBlock: BlockViewModel,
  range: ElementRangeViewModel
) extends ElementViewModel
case class HiddenTeslaViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  range: ElementRangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  noTroopDropBlock: BlockViewModel,
  range: ElementRangeViewModel
) extends ElementViewModel
case class VillageViewModel(
  elements: Set[ElementViewModel],
  wallCompartments: Set[WallCompartmentViewModel],
  possibleInternalLargeTraps: Set[PossibleLargeTrapViewModel]
) {
  require(elements.toList.map(_.id).distinct.size == elements.size, "All ids must be unique")
}

case class HogTargetingViewModel(startPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherTargetingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherQueenAttackingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class WallCompartmentViewModel(id: String, walls: Set[String], innerTiles: Set[TileViewModel], elementIds: Set[String])
case class PossibleLargeTrapViewModel(x: Int, y: Int)
case class WizardTowerHoundTargetingViewModel(tower: String, airDefense: String)
case class MinionAttackPositionViewModel(startPosition: MapCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)

sealed trait RuleResultViewModel {
  val code: String
  val title: String
  val description: String
  val success: Boolean
}

case class HogCCLureResultViewModel(
  success: Boolean,
  targetings: Set[HogTargetingViewModel],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class ArcherAnchorResultViewModel(
  success: Boolean,
  targetings: Set[ArcherTargetingViewModel],
  aimingDefenses: Set[String],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class HighHPUnderAirDefResultViewModel(
  success: Boolean,
  outOfAirDefRange: Set[String],
  inAirDefRange: Set[String],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class AirSnipedDefenseResultViewModel(
  success: Boolean,
  attackPositions: Set[MinionAttackPositionViewModel],
  airDefenses: Set[String],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class MinimumCompartmentsResultViewModel(
  success: Boolean,
  minimumCompartments: Int,
  compartments: Set[String],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class BKSwappableResultViewModel(
  success: Boolean,
  exposedTiles: Set[TileViewModel],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class WizardTowersOutOfHoundPositionsResultViewModel(
  success: Boolean,
  outOfRange: Set[String],
  inRange: Set[WizardTowerHoundTargetingViewModel],
  houndPositions: Set[BlockViewModel],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class QueenWalkedAirDefenseResultViewModel(
  success: Boolean,
  attackings: Set[ArcherQueenAttackingViewModel],
  nonReachableAirDefs: Set[String],
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class QueenWontLeaveCompartmentRuleResultViewModel(
  success: Boolean,
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel
case class EnoughPossibleTrapLocationsRuleResultViewModel(
  success: Boolean,
  score: Double,
  minScore: Double,
  code: String,
  title: String,
  description: String
) extends RuleResultViewModel

case class ResultSummaryViewModel(shortName: String, success: Boolean)
case class AnalysisReportSummaryViewModel(townHallLevel: Int, resultSummaries: Set[ResultSummaryViewModel])

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel], timeMillis: Long)

case class CantAnalyseVillageViewModel(village: VillageViewModel, message: String)

case class ExceptionViewModel(uri: String, exceptionType: String, message: String, trace: List[String])

object ViewModelProtocol extends DefaultJsonProtocol {
  implicit object ElementJsonFormat extends RootJsonFormat[ElementViewModel] {
    def write(e: ElementViewModel): JsValue = e match {
      case d: DefenseElementViewModel => defenseElementFormat.write(d)
      case d: HiddenTeslaViewModel => hiddenTeslaFormat.write(d)
      case c: ClanCastleElementViewModel => clanCastleElementFormat.write(c)
      case s: BaseStructureElementViewModel => baseStructureElementFormat.write(s)
      case b: BaseElementViewModel => baseElementFormat.write(b)
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
      case w: WedgeElementRangeViewModel => wedgeElementRangeFormat.write(w)
      case _ => throw new RuntimeException(s"Can't render ${e.getClass.getSimpleName}")
    }

    def read(value: JsValue): ElementRangeViewModel = {
      throw new NotImplementedError()
    }
  }

  implicit val circularElementRangeFormat = jsonFormat3(CircularElementRangeViewModel)
  implicit val wedgeElementRangeFormat = jsonFormat3(WedgeElementRangeViewModel)

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val mapCoordinateFormat = jsonFormat2(MapCoordinateViewModel)
  implicit val blockFormat = jsonFormat3(BlockViewModel)
  implicit val tileFormat = jsonFormat2(TileViewModel)
  implicit val wallCompartmentFormat = jsonFormat4(WallCompartmentViewModel)
  implicit val possibleLargeTrapFormat = jsonFormat2(PossibleLargeTrapViewModel)

  implicit val baseElementFormat = jsonFormat4(BaseElementViewModel)
  implicit val baseStructureElementFormat = jsonFormat5(BaseStructureElementViewModel)
  implicit val hiddenTeslaFormat = jsonFormat5(HiddenTeslaViewModel)
  implicit val defenseElementFormat = jsonFormat6(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat6(ClanCastleElementViewModel)

  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val minionAttackPositionFormat = jsonFormat3(MinionAttackPositionViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)
  implicit val archerQueenAttackingFormat = jsonFormat3(ArcherQueenAttackingViewModel)
  implicit val wizardTowerHoundTargetingFormat = jsonFormat2(WizardTowerHoundTargetingViewModel)

  implicit val hogCCLureResultFormat = jsonFormat5(HogCCLureResultViewModel)
  implicit val archerAnchorResultFormat = jsonFormat6(ArcherAnchorResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat6(HighHPUnderAirDefResultViewModel)
  implicit val airSnipedDefenseResultFormat = jsonFormat6(AirSnipedDefenseResultViewModel)
  implicit val minimumCompartmentsResultFormat = jsonFormat6(MinimumCompartmentsResultViewModel)
  implicit val bkSwappableResultFormat = jsonFormat5(BKSwappableResultViewModel)
  implicit val wizardTowersOutOfHoundPositionsResultFormat = jsonFormat7(WizardTowersOutOfHoundPositionsResultViewModel)
  implicit val queenWalkedAirDefenseResultFormat = jsonFormat6(QueenWalkedAirDefenseResultViewModel)
  implicit val queenWontLeaveCompartmentResultFormat = jsonFormat4(QueenWontLeaveCompartmentRuleResultViewModel)
  implicit val enoughPossibleTrapLocationsResultFormat = jsonFormat6(EnoughPossibleTrapLocationsRuleResultViewModel)

  implicit val villageFormat = jsonFormat3(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat3(AnalysisReportViewModel)
  implicit val cantAnalyseVillageFormat = jsonFormat2(CantAnalyseVillageViewModel)

  implicit val resultSummaryFormat = jsonFormat2(ResultSummaryViewModel)
  implicit val analysisReportSummaryFormat = jsonFormat2(AnalysisReportSummaryViewModel)

  implicit val exceptionFormat = jsonFormat4(ExceptionViewModel)
}

class ViewModelMapper {
  def exception(uri: Uri, e: Exception): ExceptionViewModel = {
    ExceptionViewModel(
      uri.toString,
      e.getClass.getName,
      e.getMessage,
      e.getStackTrace
        .map((el: StackTraceElement) => s"${el.getClassName}.${el.getMethodName} - ${el.getFileName}:${el.getLineNumber}")
        .toList
    )
  }

  def analysisSummary(userName: String, report: AnalysisReport, time: Duration): AnalysisReportSummaryViewModel = {
    AnalysisReportSummaryViewModel(
      report.village.townHallLevel.get,
      report.results.map(r => ResultSummaryViewModel(r.ruleDetails.shortName, r.success))
    )
  }

  def analysisReport(report: AnalysisReport, time: Duration): AnalysisReportViewModel = {
    AnalysisReportViewModel(
      village(report.village),
      report.results.map(ruleResult),
      time.toMillis
    )
  }

  def cantAnalyseVillage(invalidVillage: Village, message: String): CantAnalyseVillageViewModel = {
    CantAnalyseVillageViewModel(village(invalidVillage), message)
  }

  private def minionAttackPosition(position: MinionAttackPosition): MinionAttackPositionViewModel = {
    MinionAttackPositionViewModel(
      mapCoordinate(position.startPosition),
      objectId(position.targeting),
      tileCoordinate(position.hitPoint)
    )
  }

  private def ruleResult(result: RuleResult): RuleResultViewModel = {
    result match {
      case h: HogCCLureRuleResult => HogCCLureResultViewModel(
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.minBy(_.distance))
          .map(hogTargeting)
          .toSet,
        h.ruleDetails.code,
        h.ruleDetails.name,
        h.ruleDetails.description
      )
      case h: ArcherAnchorRuleResult => ArcherAnchorResultViewModel(
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.head)
          .map(archerTargeting)
          .toSet,
        h.aimingDefenses.map(objectId),
        h.ruleDetails.code,
        h.ruleDetails.name,
        h.ruleDetails.description
      )
      case a: HighHPUnderAirDefRuleResult => HighHPUnderAirDefResultViewModel(
        a.success,
        a.outOfAirDefRange.map(objectId),
        a.inAirDefRange.map(objectId),
        a.ruleDetails.code,
        a.ruleDetails.name,
        a.ruleDetails.description
      )
      case a: AirSnipedDefenseRuleResult => AirSnipedDefenseResultViewModel(
        a.success,
        a.snipedDefenses.map(minionAttackPosition),
        a.airDefenses.map(objectId),
        a.ruleDetails.code,
        a.ruleDetails.name,
        a.ruleDetails.description
      )
      case m: MinimumCompartmentsRuleResult => MinimumCompartmentsResultViewModel(
        m.success,
        m.minimumCompartments,
        m.buildingCompartments.map(objectId),
        m.ruleDetails.code,
        m.ruleDetails.name,
        m.ruleDetails.description
      )
      case b: BKSwappableRuleResult => BKSwappableResultViewModel(
        b.success,
        b.exposedTiles.map(tile),
        b.ruleDetails.code,
        b.ruleDetails.name,
        b.ruleDetails.description
      )
      case w: WizardTowersOutOfHoundPositionsRuleResult => WizardTowersOutOfHoundPositionsResultViewModel(
        w.success,
        w.outOfRange.map(objectId),
        w.inRange.map(wizardTowerHoundTargeting),
        w.houndPositions.map(_.block).map(block),
        w.ruleDetails.code,
        w.ruleDetails.name,
        w.ruleDetails.description
      )
      case q: QueenWalkedAirDefenseRuleResult => QueenWalkedAirDefenseResultViewModel(
        q.success,
        q.attackings.map(archerQueenAttacking),
        q.nonReachableAirDefs.map(objectId),
        q.ruleDetails.code,
        q.ruleDetails.name,
        q.ruleDetails.description
      )
      case q: QueenWontLeaveCompartmentRuleResult => QueenWontLeaveCompartmentRuleResultViewModel(
        q.success,
        q.ruleDetails.code,
        q.ruleDetails.name,
        q.ruleDetails.description
      )
      case t: EnoughPossibleTrapLocationsRuleResult => EnoughPossibleTrapLocationsRuleResultViewModel(
        t.success,
        t.score,
        t.minScore,
        t.ruleDetails.code,
        t.ruleDetails.name,
        t.ruleDetails.description
      )
      case _ => throw new RuntimeException(s"Don't know how to create view model for ${result.getClass.getSimpleName}")
    }
  }

  private def archerQueenAttacking(attacking: ArcherQueenAttacking): ArcherQueenAttackingViewModel = {
    ArcherQueenAttackingViewModel(
      tileCoordinate(attacking.startPosition),
      objectId(attacking.targeting),
      tileCoordinate(attacking.hitPoint)
    )
  }

  private def hogTargeting(targeting: HogTargeting): HogTargetingViewModel = {
    HogTargetingViewModel(
      tileCoordinate(targeting.startPosition),
      objectId(targeting.targeting),
      tileCoordinate(targeting.hitPoint)
    )
  }

  private def archerTargeting(targeting: ArcherTargeting): ArcherTargetingViewModel = {
    ArcherTargetingViewModel(
      tileCoordinate(targeting.standingPosition),
      objectId(targeting.targeting),
      tileCoordinate(targeting.hitPoint)
    )
  }

  private def wallCompartment(compartment: WallCompartment): WallCompartmentViewModel = {
    WallCompartmentViewModel(
      objectId(compartment),
      compartment.walls.map(objectId),
      compartment.innerTiles.map(tile),
      compartment.elements.map(objectId)
    )
  }

  private def tile(tile: Tile): TileViewModel = TileViewModel(tile.x, tile.y)

  private def village(village: Village): VillageViewModel = {
    VillageViewModel(
      village.elements.map(element),
      village.wallCompartments.map(wallCompartment),
      village.possibleInternalLargeTraps.map(possibleInternalLargeTrap)
    )
  }

  private def possibleInternalLargeTrap(trap: PossibleLargeTrap): PossibleLargeTrapViewModel = {
    PossibleLargeTrapViewModel(trap.tile.x, trap.tile.y)
  }

  private def element(element: Element): ElementViewModel = {
    val typeName = element.getClass.getSimpleName
    element match {
      case d: StationaryDefensiveBuilding =>
        d match {
          case p: PreventsTroopDrop => DefenseElementViewModel (
            objectId (d),
            typeName,
            element.level,
            block(d.block),
            block(p.preventTroopDropBlock),
            elementRange(d.range)
          )
          case h: HiddenTesla => HiddenTeslaViewModel(
            objectId (d),
            typeName,
            element.level,
            block(d.block),
            elementRange (d.range)
          )
          case _ => throw new RuntimeException(s"Can't map ${element.getClass.getSimpleName}")
        }
      case h: HeroAltar => DefenseElementViewModel (
        objectId(h),
        typeName,
        element.level,
        block(h.block),
        block(h.preventTroopDropBlock),
        elementRange(h.range)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        objectId(c),
        typeName,
        c.level,
        block(c.block),
        block(c.preventTroopDropBlock),
        elementRange(c.range)
      )
      case s: PreventsTroopDrop => BaseStructureElementViewModel(
        objectId(s),
        typeName,
        s.level,
        block(s.block),
        block(s.preventTroopDropBlock)
      )
      case e: Element => throw new RuntimeException(s"Can't map ${element.getClass.getSimpleName}")
    }
  }

  private def wizardTowerHoundTargeting(targeting: WizardTowerHoundTargeting): WizardTowerHoundTargetingViewModel = {
    WizardTowerHoundTargetingViewModel(objectId(targeting.tower), objectId(targeting.airDefense))
  }

  // TODO: Look up compile time checking for matches
  private def elementRange(range: ElementRange): ElementRangeViewModel = {
    range match {
      case c: CircularElementRange => CircularElementRangeViewModel(0, c.size)
      case b: BlindSpotCircularElementRange => CircularElementRangeViewModel(b.innerSize, b.outerSize)
      case w: WedgeElementRange => WedgeElementRangeViewModel(w.angle, w.size)
      case _ => throw new RuntimeException("Can't render element range")
    }

  }

  private def block(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.size)
  }

  private def tileCoordinate(coord: TileCoordinate): TileCoordinateViewModel = {
    TileCoordinateViewModel(coord.x, coord.y)
  }

  private def mapCoordinate(coord: MapCoordinate): MapCoordinateViewModel = {
    MapCoordinateViewModel(coord.x, coord.y)
  }

  private def objectId(obj: Object): String = {
    new String(Base64.getEncoder.encode(UUID.nameUUIDFromBytes(obj.toString.getBytes).toString.getBytes)).substring(0, 9)
  }
}
