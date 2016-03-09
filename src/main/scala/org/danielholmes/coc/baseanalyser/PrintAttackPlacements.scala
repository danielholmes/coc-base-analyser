package org.danielholmes.coc.baseanalyser

object PrintAttackPlacements extends App with Services {
  if (args.length != 1) {
    throw new RuntimeException("Must provide userName arg")
  }

  val userName = args(0)
  val village = villageGatherer.gatherByUserName(userName)
  if (village.isEmpty) {
    println(s"userName $userName not found")
    System.exit(-1)
  }

  println(stringAttackPlacementsDisplayer.build(village.get))
}
