package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

class BaseAnalyser(rules: Set[Rule]) {

}

object BaseAnalyser {
  val analysers = Map(
    8 -> new BaseAnalyser(Set(new HogCCLureRule))
  )

  def getByBase(base: Village): Option[BaseAnalyser] = {
    base.townHallLevel
      .flatMap(analysers.get)
  }
}
