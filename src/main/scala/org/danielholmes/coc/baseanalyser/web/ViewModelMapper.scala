package org.danielholmes.coc.baseanalyser.web

import java.util.UUID

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, HogTargeting}
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

class ViewModelMapper {
  def viewModel(report: AnalysisReport): AnalysisReportViewModel = {
    AnalysisReportViewModel(
      viewModel(report.village),
      report.results.map(viewModel)
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
        h.aimingDefenses.map(elementId)
      )
      case a: HighHPUnderAirDefRuleResult => HighHPUnderAirDefResultViewModel(
        a.success,
        a.outOfAirDefRange.map(elementId),
        a.inAirDefRange.map(elementId)
      )
      case a: AirSnipedDefenseRuleResult => AirSnipedDefenseResultViewModel(
        a.success,
        a.snipedDefenses.map(_.element).map(elementId),
        a.airDefenses.map(elementId)
      )
      case m: MinimumCompartmentsRuleResult => MinimumCompartmentsResultViewModel(
        m.success,
        m.minimumCompartments,
        m.compartments.map(viewModel)
      )
      case b: BKSwappableRuleResult => BKSwappableResultViewModel(
        b.success,
        b.exposedTiles.map(viewModel)
      )
      case _ => throw new RuntimeException(s"Don't know how to create view model for ${result.getClass.getSimpleName}")
    }
  }

  private def viewModel(targeting: HogTargeting): HogTargetingViewModel = {
    HogTargetingViewModel(
      viewModel(targeting.startPosition),
      elementId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  private def viewModel(targeting: ArcherTargeting): ArcherTargetingViewModel = {
    ArcherTargetingViewModel(
      viewModel(targeting.standingPosition),
      elementId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  private def viewModel(compartment: WallCompartment): WallCompartmentViewModel = {
    WallCompartmentViewModel(compartment.walls.map(elementId), compartment.innerTiles.map(viewModel))
  }

  private def viewModel(tile: Tile): TileViewModel = TileViewModel(tile.x, tile.y)

  private def viewModel(village: Village): VillageViewModel = VillageViewModel(village.elements.map(viewModel))

  private def viewModel(element: Element): ElementViewModel = {
    element match {
      case d: Defense => DefenseElementViewModel(
        elementId(d),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        viewModel(element.preventTroopDropBlock),
        viewModel(d.range)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        elementId(c),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        viewModel(element.preventTroopDropBlock),
        viewModel(c.range)
      )
      case _ => BaseElementViewModel(
        elementId(element),
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

  private def elementId(element: Element): String = {
    UUID.nameUUIDFromBytes(element.toString.getBytes).toString
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
  def preventTroopDropBlock: BlockViewModel
}
case class BaseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val preventTroopDropBlock: BlockViewModel
) extends ElementViewModel
case class DefenseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val preventTroopDropBlock: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  override val preventTroopDropBlock: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class VillageViewModel(elements: Set[ElementViewModel]) {
  require(elements.toList.map(_.id).distinct.size == elements.size, "All ids must be unique")
}

case class HogTargetingViewModel(startPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherTargetingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class WallCompartmentViewModel(walls: Set[String], innerTiles: Set[TileViewModel])

sealed trait RuleResultViewModel {
  val name: String
  val success: Boolean
}

case class HogCCLureResultViewModel(success: Boolean, targetings: Set[HogTargetingViewModel], name: String = "HogCCLure") extends RuleResultViewModel
case class ArcherAnchorResultViewModel(success: Boolean, targetings: Set[ArcherTargetingViewModel], aimingDefenses: Set[String], name: String = "ArcherAnchor") extends RuleResultViewModel
case class HighHPUnderAirDefResultViewModel(success: Boolean, outOfAirDefRange: Set[String], inAirDefRange: Set[String], name: String = "HighHPUnderAirDef") extends RuleResultViewModel
case class AirSnipedDefenseResultViewModel(success: Boolean, snipedDefenses: Set[String], airDefenses: Set[String], name: String = "AirSnipedDefense") extends RuleResultViewModel
case class MinimumCompartmentsResultViewModel(success: Boolean, minimumCompartments: Int, compartments: Set[WallCompartmentViewModel], name: String = "MinimumCompartments") extends RuleResultViewModel
case class BKSwappableResultViewModel(success: Boolean, exposedTiles: Set[TileViewModel], name: String = "BKSwappable") extends RuleResultViewModel

case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel])

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
  implicit val wallCompartmentFormat = jsonFormat2(WallCompartmentViewModel)

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

  implicit val villageFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat2(AnalysisReportViewModel)
  implicit val cantAnalyseVillageFormat = jsonFormat2(CantAnalyseVillageViewModel)
}
