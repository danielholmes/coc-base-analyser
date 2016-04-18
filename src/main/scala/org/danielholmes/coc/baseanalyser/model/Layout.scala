package org.danielholmes.coc.baseanalyser.model

object Layout extends Enumeration {
  type Layout = Value
  val Home, War = Value

  def getByCode(code: String): Option[Layout] = {
    code match {
      case "war" => Some(War)
      case "home" => Some(Home)
      case _ => None
    }
  }
}
