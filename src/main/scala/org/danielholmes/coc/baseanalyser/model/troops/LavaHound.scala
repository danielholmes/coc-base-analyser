package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.defense.AirDefense
import org.danielholmes.coc.baseanalyser.model.Block

object LavaHound {
  def getRestingArea(airDefense: AirDefense): Block = {
    airDefense.block.contractBy(1)
  }
}
