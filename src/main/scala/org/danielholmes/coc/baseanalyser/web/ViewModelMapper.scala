package org.danielholmes.coc.baseanalyser.web

import java.time.Duration
import java.util.{Base64, UUID}

import org.danielholmes.coc.baseanalyser.analysis._
import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.HiddenTesla
import org.danielholmes.coc.baseanalyser.model.heroes.HeroAltar
import org.danielholmes.coc.baseanalyser.model.range.{BlindSpotCircularElementRange, CircularElementRange, ElementRange, BlindSpotSectorElementRange}
import org.danielholmes.coc.baseanalyser.model.special.ClanCastle
import org.danielholmes.coc.baseanalyser.model.troops._
import spray.http.Uri

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
      report.results.map(r => ResultSummaryViewModel(r.result.ruleDetails.shortName, r.result.success))
    )
  }

  def analysisReport(report: AnalysisReport): AnalysisReportViewModel = {
    AnalysisReportViewModel(village(report.village), report.results.map(invocation => ruleResult(invocation.result)))
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
        m.buildingCompartments.map(objectId),
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
      case q: QueenWontLeaveCompartmentRuleResult => QueenWontLeaveCompartmentRuleResultViewModel(
        q.success,
        q.ruleDetails.code,
        q.ruleDetails.name,
        q.ruleDetails.description
      )
      case t: EnoughPossibleTrapLocationsRuleResult => EnoughPossibleTrapLocationsRuleResultViewModel(
        t.success,
        t.score,
        t.minScore,
        t.ruleDetails.code,
        t.ruleDetails.name,
        t.ruleDetails.description
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
      compartment.innerTiles.map(tile),
      compartment.elements.map(objectId)
    )
  }

  private def tile(tile: Tile): TileViewModel = TileViewModel(tile.x, tile.y)

  private def village(village: Village): VillageViewModel = {
    VillageViewModel(
      village.elements.map(element),
      village.wallCompartments.map(wallCompartment),
      village.possibleInternalLargeTraps.map(possibleInternalLargeTrap)
    )
  }

  private def possibleInternalLargeTrap(trap: PossibleLargeTrap): PossibleLargeTrapViewModel = {
    PossibleLargeTrapViewModel(trap.tile.x, trap.tile.y)
  }

  private def element(element: Element): ElementViewModel = {
    val typeName = element.getClass.getSimpleName
    element match {
      case d: StationaryDefensiveBuilding =>
        d match {
          case p: PreventsTroopDrop => DefenseElementViewModel (
            objectId (d),
            typeName,
            element.level,
            block(d.block),
            block(p.preventTroopDropBlock),
            elementRange(d.range)
          )
          case h: HiddenTesla => HiddenTeslaViewModel(
            objectId (d),
            typeName,
            element.level,
            block(d.block),
            elementRange (d.range)
          )
          case _ => throw new RuntimeException(s"Can't map ${element.getClass.getSimpleName}")
        }
      case h: HeroAltar => DefenseElementViewModel (
        objectId(h),
        typeName,
        element.level,
        block(h.block),
        block(h.preventTroopDropBlock),
        elementRange(h.range)
      )
      case c: ClanCastle => ClanCastleElementViewModel(
        objectId(c),
        typeName,
        c.level,
        block(c.block),
        block(c.preventTroopDropBlock),
        elementRange(c.range)
      )
      case s: PreventsTroopDrop => BaseStructureElementViewModel(
        objectId(s),
        typeName,
        s.level,
        block(s.block),
        block(s.preventTroopDropBlock)
      )
      case e: Element => throw new RuntimeException(s"Can't map ${element.getClass.getSimpleName}")
    }
  }

  private def wizardTowerHoundTargeting(targeting: WizardTowerHoundTargeting): WizardTowerHoundTargetingViewModel = {
    WizardTowerHoundTargetingViewModel(
      objectId(targeting.tower),
      objectId(targeting.airDefense),
      block(targeting.houndTarget)
    )
  }

  private def elementRange(range: ElementRange): ElementRangeViewModel = {
    range match {
      case c: CircularElementRange => CircularElementRangeViewModel(0, c.size)
      case b: BlindSpotCircularElementRange => CircularElementRangeViewModel(b.innerSize, b.outerSize)
      case w: BlindSpotSectorElementRange => BlindSpotSectorElementRangeViewModel(
        w.angle.degrees,
        w.angleSize.degrees,
        w.innerSize,
        w.outerSize
      )
      case _ => throw new RuntimeException("Can't render element range")
    }

  }

  private def block(block: Block): BlockViewModel = {
    BlockViewModel(block.x, block.y, block.size)
  }

  private def tileCoordinate(coord: TileCoordinate): TileCoordinateViewModel = {
    TileCoordinateViewModel(coord.x, coord.y)
  }

  private def mapCoordinate(coord: FloatMapCoordinate): MapCoordinateViewModel = {
    MapCoordinateViewModel(coord.x, coord.y)
  }

  private def objectId(obj: Object): String = {
    new String(Base64.getEncoder.encode(UUID.nameUUIDFromBytes(obj.toString.getBytes).toString.getBytes)).substring(0, 9)
  }
}
