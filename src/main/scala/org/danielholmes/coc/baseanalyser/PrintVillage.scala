package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.model.Layout

object PrintVillage extends App with Services {
  if (args.length != 1) {
    throw new RuntimeException("Must provide userName arg")
  }

  val userName = args(0)
  val village = villageGatherer.gatherByUserName(userName, Layout.Home)
  if (village.isEmpty) {
    println(s"userName $userName not found")
    System.exit(-1)
  }

  println(stringDisplayer.buildColoured(village.get))
}
