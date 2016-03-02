package org.danielholmes.coc.baseanalyser.model

case class Base(val elements: Set[Element])

object Base {
  val empty = Base(Set.empty)
}