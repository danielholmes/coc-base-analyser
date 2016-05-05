package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.PosZDouble

object ArcherQueen extends Troop {
  val Range = PosZDouble(5)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getAnyBuildingsTargets(village)
  }
}
