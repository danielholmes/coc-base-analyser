package org.danielholmes.coc.baseanalyser

object PrintVillage extends App with Services {
  if (args.length != 2) {
    throw new RuntimeException("Must provide clan code and userName arg")
  }

  val clanCode = args(0)
  val userName = args(1)

  println(
    facades.getWarVillageByUserName(clanCode, userName)
      .map(stringDisplayer.buildColoured)
      .recover(s => s)
      .get
  )
}
