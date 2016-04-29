package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait DelayedActivation extends Defense {
  val deploymentSpaceRequired: PosInt
}
