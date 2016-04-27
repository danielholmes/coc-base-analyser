package org.danielholmes.coc.baseanalyser.model

object Layout extends Enumeration {
  type Layout = Value

  val Home = Value(1, "home")
  val War = Value(2, "war")

  def getDescription(layout: Layout): String = {
    layout match {
      case War => "Active War Base"
      case Home => "Home Base"
    }
  }
}
