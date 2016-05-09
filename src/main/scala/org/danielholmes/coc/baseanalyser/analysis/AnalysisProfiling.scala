package org.danielholmes.coc.baseanalyser.analysis

import java.time.Duration

case class AnalysisProfiling(buildingBlocks: Map[String, Duration], rules: Map[RuleDetails, Duration]) {
  lazy val rulesDuration = rules.values.fold(Duration.ZERO)(_.plus(_))
  lazy val buildingBlocksDuration = buildingBlocks.values.fold(Duration.ZERO)(_.plus(_))

  lazy val rulesSorted: List[(RuleDetails, Duration)] = rules.toList.sortBy(_._2).reverse
  lazy val buildingBlocksSorted: List[(String, Duration)] = buildingBlocks.toList.sortBy(_._2).reverse

  lazy val total: Duration = rulesDuration.plus(buildingBlocksDuration)
}
