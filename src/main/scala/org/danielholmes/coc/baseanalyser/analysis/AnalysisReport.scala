package org.danielholmes.coc.baseanalyser.analysis

import org.danielholmes.coc.baseanalyser.model.Village

case class AnalysisReport(village: Village, results: Set[RuleInvocation])
