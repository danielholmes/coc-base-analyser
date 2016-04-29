package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.scalactic.anyvals.{PosInt, PosZDouble}

object HogRider extends Troop {
  val Range = PosZDouble(0)

  val HousingSpace = PosInt(5)

  override protected def getPrioritisedTargets(village: Village): List[Set[Structure]] = {
    getDefenseTargetingTargets(village)
  }
}
