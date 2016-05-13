package org.danielholmes.coc.baseanalyser.web

case class TileCoordinateViewModel(x: Int, y: Int)
case class MapCoordinateViewModel(x: Double, y: Double)

sealed trait ElementRangeViewModel {
  val typeName: String
}
case class CircularElementRangeViewModel(inner: Double, outer: Double, override val typeName: String = "Circular") extends ElementRangeViewModel
case class BlindSpotSectorElementRangeViewModel(
  angle: Double,
  angleSize: Double,
  innerSize: Double,
  outerSize: Double,
  override val typeName: String = "Sector"
) extends ElementRangeViewModel

case class BlockViewModel(x: Int, y: Int, size: Int)
case class TileViewModel(x: Int, y: Int)

sealed trait ElementViewModel {
  def id: String
  def typeName: String
  def block: BlockViewModel
}
case class DecorationElementViewModel(
  override val id: String,
  override val typeName: String,
  override val block: BlockViewModel
) extends ElementViewModel
case class TrapElementViewModel(
  override val id: String,
  override val typeName: String,
  level: Int,
  override val block: BlockViewModel
) extends ElementViewModel
case class BaseStructureElementViewModel(
  override val id: String,
  override val typeName: String,
  level: Int,
  override val block: BlockViewModel,
  noTroopDropBlock: BlockViewModel
) extends ElementViewModel
case class DefenseElementViewModel(
  override val id: String,
  override val typeName: String,
  level: Int,
  override val block: BlockViewModel,
  noTroopDropBlock: BlockViewModel,
  range: ElementRangeViewModel
) extends ElementViewModel
case class HiddenTeslaViewModel(
  override val id: String,
  override val typeName: String,
  level: Int,
  override val block: BlockViewModel,
  range: ElementRangeViewModel
) extends ElementViewModel
case class ClanCastleElementViewModel(
  override val id: String,
  override val typeName: String,
  level: Int,
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
case class WizardTowerHoundTargetingViewModel(tower: String, airDefense: String, houndTarget: BlockViewModel)
case class MinionAttackPositionViewModel(startPosition: MapCoordinateViewModel, targetingId: String, hitPoint: TileCoordinateViewModel)
