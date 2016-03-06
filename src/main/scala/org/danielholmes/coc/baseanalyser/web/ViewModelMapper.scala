package org.danielholmes.coc.baseanalyser.web

import org.danielholmes.coc.baseanalyser.analysis.AnalysisReport
import org.danielholmes.coc.baseanalyser.model._
import spray.json.{JsValue, RootJsonFormat, DefaultJsonProtocol}

class ViewModelMapper {
  def viewModel(report: AnalysisReport): AnalysisReportViewModel = {
    AnalysisReportViewModel(viewModel(report.village))
  }

  def viewModel(village: Village): VillageViewModel = {
    VillageViewModel(viewModel(village.elements))
  }

  def viewModel(elements: Set[Element]): Set[ElementViewModel] = {
    elements.map(viewModel)
  }

  def viewModel(element: Element): ElementViewModel = {
    element match {
      case d: Defense => DefenseElementViewModel(
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        RangeViewModel(d.range.innerSize.toInt, d.range.outerSize.toInt)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block),
        RangeViewModel(c.range.innerSize.toInt, c.range.outerSize.toInt)
      )
      case _ => BaseElementViewModel(
        element.getClass.getSimpleName,
        element.level,
        viewModel(element.block)
      )
    }
  }

  def viewModel(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.width.toInt, block.height.toInt)
  }
}

case class RangeViewModel(inner: Int, outer: Int)
case class BlockViewModel(x: Int, y: Int, width: Int, height: Int)
sealed trait ElementViewModel {
  def typeName: String
  def level: Int
  def block: BlockViewModel
}
case class BaseElementViewModel(
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel
) extends ElementViewModel
case class DefenseElementViewModel(
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val typeName: String,
  override val level: Int,
  override val block: BlockViewModel,
  range: RangeViewModel
) extends ElementViewModel
case class VillageViewModel(elements: Set[ElementViewModel])
case class AnalysisReportViewModel(village: VillageViewModel)


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

  implicit val rangeFormat = jsonFormat2(RangeViewModel)
  implicit val blockFormat = jsonFormat4(BlockViewModel)
  implicit val baseElementFormat = jsonFormat3(BaseElementViewModel)
  implicit val defenseElementFormat = jsonFormat4(DefenseElementViewModel)
  implicit val clanCastleElementFormat = jsonFormat4(ClanCastleElementViewModel)
  implicit val villageFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportFormat = jsonFormat1(AnalysisReportViewModel)
}
