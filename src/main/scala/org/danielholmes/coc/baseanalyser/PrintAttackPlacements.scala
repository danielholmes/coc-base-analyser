package org.danielholmes.coc.baseanalyser

object PrintAttackPlacements extends App with Services {
  if (args.length != 2) {
    throw new RuntimeException("Must provide clan code and userName arg")
  }

  val clanCode = args(0)
  val userName = args(1)

  println(
    facades.getWarVillageByUserName(clanCode, userName)
      .map(stringTroopDropDisplayer.build)
      .recover(s => s)
      .get
  )
}
