package org.danielholmes.coc.baseanalyser.web

import java.time.Duration
import java.util.{Base64, UUID}

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops._
import spray.http.Uri
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

case class TileCoordinateViewModel(x: Int, y: Int)
case class MapCoordinateViewModel(x: Double, y: Double)
case class RangeViewModel(inner: Double, outer: Double)
case class BlockViewModel(x: Int, y: Int, size: Int)
case class TileViewModel(x: Int, y: Int)
sealed trait ElementViewModel {
  def id: String
  def typeName: String
  def level: Int
  def block: BlockViewModel
  def noTroopDropBlock: BlockViewModel
}
case class BaseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val noTroopDropBlock: BlockViewModel
) extends ElementViewModel
case class DefenseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val noTroopDropBlock: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val noTroopDropBlock: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class VillageViewModel(elements: Set[ElementViewModel], wallCompartments: Set[WallCompartmentViewModel]) {
  require(elements.toList.map(_.id).distinct.size == elements.size, "All ids must be unique")
}

case class HogTargetingViewModel(startPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherTargetingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherQueenAttackingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class WallCompartmentViewModel(id: String, walls: Set[String], innerTiles: Set[TileViewModel])
case class WizardTowerHoundTargetingViewModel(tower: String, airDefense: String)
case class MinionAttackPositionViewModel(startPosition: MapCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)

sealed trait RuleResultViewModel {
  val code: String
  val title: String
  val description: String
  val success: Boolean
}

case class HogCCLureResultViewModel(success: Boolean, targetings: Set[HogTargetingViewModel], code: String, title: String, description: String) extends RuleResultViewModel
case class ArcherAnchorResultViewModel(success: Boolean, targetings: Set[ArcherTargetingViewModel], aimingDefenses: Set[String], code: String, title: String, description: String) extends RuleResultViewModel
case class HighHPUnderAirDefResultViewModel(success: Boolean, outOfAirDefRange: Set[String], inAirDefRange: Set[String], code: String, title: String, description: String) extends RuleResultViewModel
case class AirSnipedDefenseResultViewModel(success: Boolean, attackPositions: Set[MinionAttackPositionViewModel], airDefenses: Set[String], code: String, title: String, description: String) extends RuleResultViewModel
case class MinimumCompartmentsResultViewModel(success: Boolean, minimumCompartments: Int, compartments: Set[String], code: String, title: String, description: String) extends RuleResultViewModel
case class BKSwappableResultViewModel(success: Boolean, exposedTiles: Set[TileViewModel], code: String, title: String, description: String) extends RuleResultViewModel
case class WizardTowersOutOfHoundPositionsResultViewModel(success: Boolean, outOfRange: Set[String], inRange: Set[WizardTowerHoundTargetingViewModel], houndPositions: Set[BlockViewModel], code: String, title: String, description: String) extends RuleResultViewModel
case class QueenWalkedAirDefenseResultViewModel(success: Boolean, attackings: Set[ArcherQueenAttackingViewModel], nonReachableAirDefs: Set[String], code: String, title: String, description: String) extends RuleResultViewModel

case class ResultSummaryViewModel(shortName: String, success: Boolean)
case class AnalysisReportSummaryViewModel(townHallLevel: Int, resultSummaries: Set[ResultSummaryViewModel])

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel], timeMillis: Long)

case class CantAnalyseVillageViewModel(village: VillageViewModel, message: String)

case class ExceptionViewModel(uri: String, exceptionType: String, message: String, trace: List[String])

object ViewModelProtocol extends DefaultJsonProtocol {
  implicit  object ElementJsonFormat extends RootJsonFormat[ElementViewModel] {
    def write(e: ElementViewModel) = e match {
      case d: DefenseElementViewModel => defenseElementFormat.write(d)
      case c: ClanCastleElementViewModel => clanCastleElementFormat.write(c)
      case b: BaseElementViewModel => baseElementFormat.write(b)
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

  implicit  object RuleResultJsonFormat extends RootJsonFormat[RuleResultViewModel] {
    def write(r: RuleResultViewModel) = r match {
      case h: HogCCLureResultViewModel => hogCCLureResultFormat.write(h)
      case a: ArcherAnchorResultViewModel => archerAnchorResultFormat.write(a)
      case a: HighHPUnderAirDefResultViewModel => highHPUnderAirDefResultFormat.write(a)
      case a: AirSnipedDefenseResultViewModel => airSnipedDefenseResultFormat.write(a)
      case m: MinimumCompartmentsResultViewModel => minimumCompartmentsResultFormat.write(m)
      case b: BKSwappableResultViewModel => bkSwappableResultFormat.write(b)
      case w: WizardTowersOutOfHoundPositionsResultViewModel => wizardTowersOutOfHoundPositionsResultFormat.write(w)
      case q: QueenWalkedAirDefenseResultViewModel => queenWalkedAirDefenseResultFormat.write(q)
      case _ => throw new RuntimeException(s"Don't know how to serialise ${r.getClass.getSimpleName}")
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val mapCoordinateFormat = jsonFormat2(MapCoordinateViewModel)
  implicit val rangeFormat = jsonFormat2(RangeViewModel)
  implicit val blockFormat = jsonFormat3(BlockViewModel)
  implicit val tileFormat = jsonFormat2(TileViewModel)
  implicit val wallCompartmentFormat = jsonFormat3(WallCompartmentViewModel)

  implicit val baseElementFormat = jsonFormat5(BaseElementViewModel)
  implicit val defenseElementFormat = jsonFormat6(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat6(ClanCastleElementViewModel)
  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val minionAttackPositionFormat = jsonFormat3(MinionAttackPositionViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)
  implicit val archerQueenAttackingFormat = jsonFormat3(ArcherQueenAttackingViewModel)
  implicit val WizardTowerHoundTargetingFormat = jsonFormat2(WizardTowerHoundTargetingViewModel)

  implicit val hogCCLureResultFormat = jsonFormat5(HogCCLureResultViewModel)
  implicit val archerAnchorResultFormat = jsonFormat6(ArcherAnchorResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat6(HighHPUnderAirDefResultViewModel)
  implicit val airSnipedDefenseResultFormat = jsonFormat6(AirSnipedDefenseResultViewModel)
  implicit val minimumCompartmentsResultFormat = jsonFormat6(MinimumCompartmentsResultViewModel)
  implicit val bkSwappableResultFormat = jsonFormat5(BKSwappableResultViewModel)
  implicit val wizardTowersOutOfHoundPositionsResultFormat = jsonFormat7(WizardTowersOutOfHoundPositionsResultViewModel)
  implicit val queenWalkedAirDefenseResultFormat = jsonFormat6(QueenWalkedAirDefenseResultViewModel)

  implicit val villageFormat = jsonFormat2(VillageViewModel)
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
        m.compartments.map(objectId),
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
      compartment.innerTiles.map(tile)
    )
  }

  private def tile(tile: Tile): TileViewModel = TileViewModel(tile.x, tile.y)

  private def village(village: Village): VillageViewModel = {
    VillageViewModel(village.elements.map(element), village.wallCompartments.map(wallCompartment))
  }

  private def element(element: Element): ElementViewModel = {
    element match {
      case d: Defense => DefenseElementViewModel(
        objectId(d),
        element.getClass.getSimpleName,
        element.level,
        block(element.block),
        block(element.preventTroopDropBlock),
        elementRange(d.range)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        objectId(c),
        element.getClass.getSimpleName,
        element.level,
        block(element.block),
        block(element.preventTroopDropBlock),
        elementRange(c.range)
      )
      case _ => BaseElementViewModel(
        objectId(element),
        element.getClass.getSimpleName,
        element.level,
        block(element.block),
        block(element.preventTroopDropBlock)
      )
    }
  }

  private def wizardTowerHoundTargeting(targeting: WizardTowerHoundTargeting): WizardTowerHoundTargetingViewModel = {
    WizardTowerHoundTargetingViewModel(objectId(targeting.tower), objectId(targeting.airDefense))
  }

  // TODO: Look up compile time checking for matches
  private def elementRange(range: ElementRange): RangeViewModel = {
    range match {
      case c: CircularElementRange => RangeViewModel(0, c.size)
      case b: BlindSpotCircularElementRange => RangeViewModel(b.innerSize, b.outerSize)
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
