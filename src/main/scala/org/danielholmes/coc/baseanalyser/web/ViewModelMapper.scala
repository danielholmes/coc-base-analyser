package org.danielholmes.coc.baseanalyser.web

import org.danielholmes.coc.baseanalyser.analysis.AnalysisReport
import org.danielholmes.coc.baseanalyser.model.{Block, Village, Element}
import spray.json.DefaultJsonProtocol

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
    ElementViewModel(
      element.getClass.getSimpleName,
      viewModel(element.block)
    )
  }

  def viewModel(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.width.toInt, block.height.toInt)
  }
}

case class BlockViewModel(x: Int, y: Int, width: Int, height: Int)
case class ElementViewModel(typeName: String, block: BlockViewModel)
case class VillageViewModel(elements: Set[ElementViewModel])
case class AnalysisReportViewModel(village: VillageViewModel)

object ViewModelProtocol extends DefaultJsonProtocol {
  implicit val blockViewModelFormat = jsonFormat4(BlockViewModel)
  implicit val elementViewModelFormat = jsonFormat2(ElementViewModel)
  implicit val villageViewModelFormat = jsonFormat1(VillageViewModel)
  implicit val analysisReportViewModelFormat = jsonFormat1(AnalysisReportViewModel)
}
