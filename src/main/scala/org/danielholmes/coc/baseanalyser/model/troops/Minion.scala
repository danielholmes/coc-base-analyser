package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Structure, Village}
import org.scalactic.anyvals.PosZDouble

object Minion extends Troop {
  val Range = PosZDouble(0.75)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getAnyBuildingsTargets(village)
  }
}
