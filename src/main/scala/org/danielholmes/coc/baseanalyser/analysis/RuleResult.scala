package org.danielholmes.coc.baseanalyser.analysis

trait RuleResult {
  val success: Boolean

  val ruleDetails: RuleDetails
}
