package org.danielholmes.coc.baseanalyser.analysis

trait RuleResult {
  val success: Boolean
}

class SuccessRuleResult extends RuleResult {
  val success: Boolean = true
}

object RuleResult {
  def pass: RuleResult = new SuccessRuleResult
}