package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Structure, Village}
import org.scalactic.anyvals.{PosInt, PosZDouble}

object Archer extends Troop {
  val Range = PosZDouble(3.5)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getAnyBuildingsTargets(village)
  }
}
