package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model.Element

abstract class ElementFactory {
  def build(raw: RawBuilding): Option[Element]
}