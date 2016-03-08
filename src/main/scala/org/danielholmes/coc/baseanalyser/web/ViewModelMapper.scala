package org.danielholmes.coc.baseanalyser.web

import java.util.UUID

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
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
        RangeViewModel(d.range.innerSize.toInt, d.range.outerSize.toInt)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        elementId(c),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        RangeViewModel(c.range.innerSize.toInt, c.range.outerSize.toInt)
      )
      case _ => BaseElementViewModel(
        elementId(element),
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block)
      )
    }
  }

  def viewModel(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.width.toInt, block.height.toInt)
  }

  def viewModel(coord: TileCoordinate): TileCoordinateViewModel = {
    TileCoordinateViewModel(coord.x, coord.y)
  }

  private def elementId(element: Element): String = {
    UUID.nameUUIDFromBytes(element.toString().getBytes()).toString()
  }
}

case class TileCoordinateViewModel(x: Int, y: Int)
case class RangeViewModel(inner: Int, outer: Int)
case class BlockViewModel(x: Int, y: Int, width: Int, height: Int)
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
case class DefenseElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val id: String,
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
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
      case a: HighHPUnderAirDefFailedResultViewModel => highHPUnderAirDefResultFormat.write(a)
      case _ => throw new RuntimeException(s"Don't know how to serialise ${r.getClass.getSimpleName}")
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

  implicit val tileCoordinateFormat = jsonFormat2(TileCoordinateViewModel)
  implicit val rangeFormat = jsonFormat2(RangeViewModel)
  implicit val blockFormat = jsonFormat4(BlockViewModel)

  implicit val baseElementFormat = jsonFormat4(BaseElementViewModel)
  implicit val defenseElementFormat = jsonFormat5(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat5(ClanCastleElementViewModel)

  implicit val successRuleResultFormat = jsonFormat2(SuccessRuleResultViewModel)
  implicit val hogTargetingFormat = jsonFormat3(HogTargetingViewModel)
  implicit val hogCCLureFormat = jsonFormat3(HogCCLureFailedResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat3(HighHPUnderAirDefFailedResultViewModel)

  implicit val villageFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat2(AnalysisReportViewModel)
}
