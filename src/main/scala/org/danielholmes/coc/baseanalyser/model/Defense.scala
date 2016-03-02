package org.danielholmes.coc.baseanalyser.model

import Target._

trait Defense {
  def range: Range
  def targets: Set[Target]

  require(targets.nonEmpty, "Must have some targets")
}
