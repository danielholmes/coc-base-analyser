package org.danielholmes.coc.baseanalyser.web

import java.util.UUID

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, HogTargeting}
import spray.json.{JsValue, RootJsonFormat, DefaultJsonProtocol}

class ViewModelMapper {
  def viewModel(report: AnalysisReport): AnalysisReportViewModel = {
    AnalysisReportViewModel(
      viewModel(report.village),
      report.results.map(viewModel)
    )
  }

  def viewModel(result: RuleResult): RuleResultViewModel = {
    result match {
      case s: SuccessRuleResult => SuccessRuleResultViewModel(result.ruleName, result.success)
      case h: HogCCLureResult => HogCCLureFailedResultViewModel(
        h.ruleName,
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.minBy(_.distance))
          .map(viewModel)
          .toSet
      )
      case h: ArcherAnchorRuleResult => ArcherAnchorFailedResultViewModel(
        h.ruleName,
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.head)
          .map(viewModel)
          .toSet
      )
      case a: HighHPUnderAirDefResult => HighHPUnderAirDefFailedResultViewModel(
        a.ruleName,
        a.success,
        a.outOfAirDefRange.map(elementId)
      )
      case _ => throw new RuntimeException(s"Don't know how to create view model for ${result.getClass.getSimpleName}")
    }
  }

  def viewModel(targeting: HogTargeting): HogTargetingViewModel = {
    HogTargetingViewModel(
      viewModel(targeting.startPosition),
      elementId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  def viewModel(targeting: ArcherTargeting): ArcherTargetingViewModel = {
    ArcherTargetingViewModel(
      viewModel(targeting.standingPosition),
      elementId(targeting.targeting),
      viewModel(targeting.hitPoint)
    )
  }

  def viewModel(village: Village): VillageViewModel = {
    VillageViewModel(village.elements.map(viewModel))
  }

  def viewModel(element: Element): ElementViewModel = {
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

  def viewModel(range: ElementRange): RangeViewModel = {
    RangeViewModel(range.innerSize.toInt, range.outerSize.toInt)
  }

  def viewModel(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.size.toInt)
  }

  def viewModel(coord: TileCoordinate): TileCoordinateViewModel = {
    TileCoordinateViewModel(coord.x, coord.y)
  }

  private def elementId(element: Element): String = {
    UUID.nameUUIDFromBytes(element.toString.getBytes).toString
  }
}

case class TileCoordinateViewModel(x: Int, y: Int)
case class RangeViewModel(inner: Int, outer: Int)
case class BlockViewModel(x: Int, y: Int, size: Int)
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

sealed trait RuleResultViewModel {
  val name: String
  val success: Boolean
}
case class SuccessRuleResultViewModel(name: String, success: Boolean) extends RuleResultViewModel
case class HogTargetingViewModel(startPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class HogCCLureFailedResultViewModel(name: String, success: Boolean, targetings: Set[HogTargetingViewModel]) extends RuleResultViewModel
case class ArcherTargetingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherAnchorFailedResultViewModel(name: String, success: Boolean, targetings: Set[ArcherTargetingViewModel]) extends RuleResultViewModel
case class HighHPUnderAirDefFailedResultViewModel(name: String, success: Boolean, outOfAirDefRange: Set[String]) extends RuleResultViewModel
case class AnalysisReportViewModel(village: VillageViewModel, results: Set[RuleResultViewModel])


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
      case h: HogCCLureFailedResultViewModel => hogCCLureFormat.write(h)
      case aa: ArcherAnchorFailedResultViewModel => archerAnchorFormat.write(aa)
      case a: HighHPUnderAirDefFailedResultViewModel => highHPUnderAirDefResultFormat.write(a)
      case _ => throw new RuntimeException(s"Don't know how to serialise ${r.getClass.getSimpleName}")
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val rangeFormat = jsonFormat2(RangeViewModel)
  implicit val blockFormat = jsonFormat3(BlockViewModel)

  implicit val baseElementFormat = jsonFormat5(BaseElementViewModel)
  implicit val defenseElementFormat = jsonFormat6(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat6(ClanCastleElementViewModel)

  implicit val successRuleResultFormat = jsonFormat2(SuccessRuleResultViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)
  implicit val hogCCLureFormat = jsonFormat3(HogCCLureFailedResultViewModel)
  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val archerAnchorFormat = jsonFormat3(ArcherAnchorFailedResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat3(HighHPUnderAirDefFailedResultViewModel)

  implicit val villageFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat2(AnalysisReportViewModel)
}
