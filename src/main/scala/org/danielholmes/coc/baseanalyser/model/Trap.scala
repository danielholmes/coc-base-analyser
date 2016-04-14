package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait Trap extends Element {
  override lazy val preventTroopDropSize: PosInt = {
    throw new RuntimeException("Same as tesla, need refactor befoore thhis works")
  }
}
