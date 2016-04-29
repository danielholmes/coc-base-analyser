package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.{Structure, Village}
import org.scalactic.anyvals.{PosZDouble, PosZInt}

object Dragon extends Troop {
  val Range = PosZDouble(1)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getAnyBuildingsTargets(village)
  }
}
