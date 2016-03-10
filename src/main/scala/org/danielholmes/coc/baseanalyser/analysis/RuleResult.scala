package org.danielholmes.coc.baseanalyser.analysis

trait RuleResult {
  val success: Boolean
  val ruleName: String
}

case class SuccessRuleResult(val ruleName: String) extends RuleResult {
  val success: Boolean = true
}

object RuleResult {
  def success(ruleName: String): RuleResult = new SuccessRuleResult(ruleName)
}