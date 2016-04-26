package org.danielholmes.coc.baseanalyser.model

object Target extends Enumeration {
  type Target = Value
  val Ground, Air = Value

  val Both = Target.values
  val GroundOnly = Set(Ground)
  val AirOnly = Set(Air)
}
