package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

trait Rule {
  def analyse(base: Village): RuleResult
}
