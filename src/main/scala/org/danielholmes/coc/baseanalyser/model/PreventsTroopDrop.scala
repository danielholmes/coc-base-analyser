package org.danielholmes.coc.baseanalyser.model

import org.scalactic.anyvals.PosInt

trait PreventsTroopDrop extends Structure {
  lazy val preventTroopDropSize = PosInt.from(size + 2).get
  lazy val preventTroopDropBlock = block.expandToSize(preventTroopDropSize)
}
