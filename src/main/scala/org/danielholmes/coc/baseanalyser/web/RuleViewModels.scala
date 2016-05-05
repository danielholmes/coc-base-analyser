package org.danielholmes.coc.baseanalyser.web

sealed trait RuleResultViewModel {
  val code: String
  val title: String
  val description: String
  val success: Boolean
}

case class ResultSummaryViewModel(shortName: String, success: Boolean)

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
