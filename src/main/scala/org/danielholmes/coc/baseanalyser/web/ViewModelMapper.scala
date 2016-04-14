package org.danielholmes.coc.baseanalyser.web

import java.time.Duration
import java.util.{Base64, UUID}

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, HogTargeting}
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

class ViewModelMapper {
  def viewModel(report: AnalysisReport, time: Duration): AnalysisReportViewModel = {
    AnalysisReportViewModel(
      viewModel(report.village),
      report.results.map(viewModel),
      time.toMillis
    )
  }

  def viewModel(village: Village, message: String): CantAnalyseVillageViewModel = {
    CantAnalyseVillageViewModel(viewModel(village), message)
  }

  private def viewModel(result: RuleResult): RuleResultViewModel = {
    result match {
      case h: HogCCLureRuleResult => HogCCLureResultViewModel(
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.minBy(_.distance))
          .map(viewModel)
          .toSet
      )
      case h: ArcherAnchorRuleResult => ArcherAnchorResultViewModel(
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.head)
          .map(viewModel)
          .toSet,
        h.aimingDefenses.map(objectId)
      )
      case a: HighHPUnderAirDefRuleResult => HighHPUnderAirDefResultViewModel(
        a.success,
        a.outOfAirDefRange.map(objectId),
        a.inAirDefRange.map(objectId)
      )
      case a: AirSnipedDefenseRuleResult => AirSnipedDefenseResultViewModel(
        a.success,
        a.snipedDefenses.map(_.element).map(objectId),
        a.airDefenses.map(objectId)
      )
      case m: MinimumCompartmentsRuleResult => MinimumCompartmentsResultViewModel(
        m.success,
        m.minimumCompartments,
        m.compartments.map(objectId)
      )
      case b: BKSwappableRuleResult => BKSwappableResultViewModel(
        b.success,
        b.exposedTiles.map(viewModel)
      )
      case w: WizardTowersOutOfHoundPositionsRuleResult => WizardTowersOutOfHoundPositionsResultViewModel(
        w.success,
        w.outOfRange.map(objectId),
        w.inRange.map(objectId),
        w.houndPositions.map(_.block).map(viewModel)
      )
      case _ => throw new RuntimeException(s"Don't know how to create view model for ${result.getClass.getSimpleName}")
    }
  }

  private def viewModel(targeting: HogTargeting): HogTargetingViewModel = {
    HogTargetingViewModel(
      viewModel(targeting.startPosition),
      objectId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  private def viewModel(targeting: ArcherTargeting): ArcherTargetingViewModel = {
    ArcherTargetingViewModel(
      viewModel(targeting.standingPosition),
      objectId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  private def viewModel(compartment: WallCompartment): WallCompartmentViewModel = {
    WallCompartmentViewModel(
      objectId(compartment),
      compartment.walls.map(objectId),
      compartment.innerTiles.map(viewModel)
    )
  }

  private def viewModel(tile: Tile): TileViewModel = TileViewModel(tile.x, tile.y)

  private def viewModel(village: Village): VillageViewModel = {
    VillageViewModel(village.elements.map(viewModel), village.wallCompartments.map(viewModel))
  }

  private def viewModel(element: Element): ElementViewModel = {
    element match {
      case d: Defense => DefenseElementViewModel(
        objectId(d),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        viewModel(element.preventTroopDropBlock),
        viewModel(d.range)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        objectId(c),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        viewModel(element.preventTroopDropBlock),
        viewModel(c.range)
      )
      case _ => BaseElementViewModel(
        objectId(element),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        viewModel(element.preventTroopDropBlock)
      )
    }
  }

  private def viewModel(range: ElementRange): RangeViewModel = {
    RangeViewModel(range.innerSize.toInt, range.outerSize.toInt)
  }

  private def viewModel(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.size.toInt)
  }

  private def viewModel(coord: TileCoordinate): TileCoordinateViewModel = {
    TileCoordinateViewModel(coord.x, coord.y)
  }

  private def objectId(obj: Object): String = {
    new String(Base64.getEncoder.encode(UUID.nameUUIDFromBytes(obj.toString.getBytes).toString.getBytes)).substring(0, 9)
  }
}

case class TileCoordinateViewModel(x: Int, y: Int)
case class RangeViewModel(inner: Int, outer: Int)
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
case class WallCompartmentViewModel(id: String, walls: Set[String], innerTiles: Set[TileViewModel])

sealed trait RuleResultViewModel {
  val name: String
  val success: Boolean
}

case class HogCCLureResultViewModel(success: Boolean, targetings: Set[HogTargetingViewModel], name: String = "HogCCLure") extends RuleResultViewModel
case class ArcherAnchorResultViewModel(success: Boolean, targetings: Set[ArcherTargetingViewModel], aimingDefenses: Set[String], name: String = "ArcherAnchor") extends RuleResultViewModel
case class HighHPUnderAirDefResultViewModel(success: Boolean, outOfAirDefRange: Set[String], inAirDefRange: Set[String], name: String = "HighHPUnderAirDef") extends RuleResultViewModel
case class AirSnipedDefenseResultViewModel(success: Boolean, snipedDefenses: Set[String], airDefenses: Set[String], name: String = "AirSnipedDefense") extends RuleResultViewModel
case class MinimumCompartmentsResultViewModel(success: Boolean, minimumCompartments: Int, compartments: Set[String], name: String = "MinimumCompartments") extends RuleResultViewModel
case class BKSwappableResultViewModel(success: Boolean, exposedTiles: Set[TileViewModel], name: String = "BKSwappable") extends RuleResultViewModel
case class WizardTowersOutOfHoundPositionsResultViewModel(success: Boolean, outOfRange: Set[String], inRange: Set[String], houndPositions: Set[BlockViewModel], name: String = "WizardTowersOutOfHoundPositions") extends RuleResultViewModel

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel], timeMillis: Long)

case class CantAnalyseVillageViewModel(village: VillageViewModel, message: String)

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
      case _ => throw new RuntimeException(s"Don't know how to serialise ${r.getClass.getSimpleName}")
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val rangeFormat = jsonFormat2(RangeViewModel)
  implicit val blockFormat = jsonFormat3(BlockViewModel)
  implicit val tileFormat = jsonFormat2(TileViewModel)
  implicit val wallCompartmentFormat = jsonFormat3(WallCompartmentViewModel)

  implicit val baseElementFormat = jsonFormat5(BaseElementViewModel)
  implicit val defenseElementFormat = jsonFormat6(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat6(ClanCastleElementViewModel)
  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)

  implicit val hogCCLureResultFormat = jsonFormat3(HogCCLureResultViewModel)
  implicit val archerAnchorResultFormat = jsonFormat4(ArcherAnchorResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat4(HighHPUnderAirDefResultViewModel)
  implicit val airSnipedDefenseResultFormat = jsonFormat4(AirSnipedDefenseResultViewModel)
  implicit val minimumCompartmentsResultFormat = jsonFormat4(MinimumCompartmentsResultViewModel)
  implicit val bkSwappableResultFormat = jsonFormat3(BKSwappableResultViewModel)
  implicit val wizardTowersOutOfHoundPositionsResultFormat = jsonFormat5(WizardTowersOutOfHoundPositionsResultViewModel)

  implicit val villageFormat = jsonFormat2(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat3(AnalysisReportViewModel)
  implicit val cantAnalyseVillageFormat = jsonFormat2(CantAnalyseVillageViewModel)
}
