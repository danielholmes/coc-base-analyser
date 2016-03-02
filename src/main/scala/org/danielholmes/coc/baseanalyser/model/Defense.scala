package org.danielholmes.coc.baseanalyser.model

import Target._

trait Defense extends Element {
  def range: Range
  def targets: Set[Target]

  // Not specified in constructor atm so doesnt work
  //require(targets.nonEmpty, "Must have some targets")
}
