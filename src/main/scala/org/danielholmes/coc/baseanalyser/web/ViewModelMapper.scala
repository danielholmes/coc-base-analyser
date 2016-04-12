package org.danielholmes.coc.baseanalyser.web

import java.util.UUID

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.troops.{ArcherTargeting, HogTargeting, MinionAttackPosition}
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

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
      case h: HogCCLureResult => HogCCLureResultViewModel(
        h.ruleName,
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.minBy(_.distance))
          .map(viewModel)
          .toSet
      )
      case h: ArcherAnchorRuleResult => ArcherAnchorResultViewModel(
        h.ruleName,
        h.success,
        h.targeting
          .groupBy(_.targeting)
          .values
          .map(_.head)
          .map(viewModel)
          .toSet,
        h.aimingDefenses.map(elementId)
      )
      case a: HighHPUnderAirDefResult => HighHPUnderAirDefResultViewModel(
        a.ruleName,
        a.success,
        a.outOfAirDefRange.map(elementId),
        a.inAirDefRange.map(elementId)
      )
      case a: AirSnipedDefenseRuleResult => AirSnipedDefenseResultViewModel(
        a.ruleName,
        a.success,
        a.snipedDefenses.map(_.element).map(elementId),
        a.airDefenses.map(elementId)
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
case class HogCCLureResultViewModel(name: String, success: Boolean, targetings: Set[HogTargetingViewModel]) extends RuleResultViewModel
case class ArcherTargetingViewModel(standingPosition: TileCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
case class ArcherAnchorResultViewModel(name: String, success: Boolean, targetings: Set[ArcherTargetingViewModel], aimingDefenses: Set[String]) extends RuleResultViewModel
case class HighHPUnderAirDefResultViewModel(name: String, success: Boolean, outOfAirDefRange: Set[String], inAirDefRange: Set[String]) extends RuleResultViewModel
case class AirSnipedDefenseResultViewModel(name: String, success: Boolean, snipedDefenses: Set[String], airDefenses: Set[String]) extends RuleResultViewModel
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
      case h: HogCCLureResultViewModel => hogCCLureFormat.write(h)
      case aa: ArcherAnchorResultViewModel => archerAnchorFormat.write(aa)
      case a: HighHPUnderAirDefResultViewModel => highHPUnderAirDefResultFormat.write(a)
      case a: AirSnipedDefenseResultViewModel => airSnipedDefenseResultViewModelFormat.write(a)
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
  implicit val hogCCLureFormat = jsonFormat3(HogCCLureResultViewModel)
  implicit val archerTargetingFormat = jsonFormat3(ArcherTargetingViewModel)
  implicit val archerAnchorFormat = jsonFormat4(ArcherAnchorResultViewModel)
  implicit val highHPUnderAirDefResultFormat = jsonFormat4(HighHPUnderAirDefResultViewModel)
  implicit val airSnipedDefenseResultViewModelFormat = jsonFormat4(AirSnipedDefenseResultViewModel)

  implicit val villageFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat2(AnalysisReportViewModel)
}
