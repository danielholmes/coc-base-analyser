package org.danielholmes.coc.baseanalyser.model

import Target._
import org.danielholmes.coc.baseanalyser.model.range.ElementRange

trait Defense extends Building {
  val range: ElementRange
  val targets: Set[Target]
}
