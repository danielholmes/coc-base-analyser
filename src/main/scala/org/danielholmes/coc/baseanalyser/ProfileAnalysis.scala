package org.danielholmes.coc.baseanalyser

import java.time.Duration

import org.danielholmes.coc.baseanalyser.model.{Tile, TileCoordinate, WallCompartment}
import org.danielholmes.coc.baseanalyser.util.TimedInvocation

object ProfileAnalysis extends App with Services {
  if (args.length != 2) {
    throw new RuntimeException("Must provide clan code and userName arg")
  }

  val clanCode = args(0)
  val userName = args(1)

  private def formatSecs(duration: Duration): String = "%.3f".format(duration.toMillis / 1000.0) + "s"

  println(
    facades.getWarVillageByUserName(clanCode, userName)
      .map(village => {
        villageAnalyser.analyse(village)
          .map(report => {
            val all = Map(
              "Building blocks" -> report.profiling.buildingBlocksSorted.map(t => (t._1, formatSecs(t._2))),
              "Rules" -> report.profiling.rulesSorted.map(t => (t._1.shortName, formatSecs(t._2))),
              "Total" -> Seq(("", formatSecs(report.profiling.total)))
            )
            val lineLength = all.values.flatMap(t => t.map(t => t._1.length + t._2.length + 1)).max
            all.mapValues(_.map(t => t._1 + (" " * (lineLength - t._1.length - t._2.length)) + t._2))
              .toSeq
              .flatMap(t => Seq("-" * lineLength + s"\n# ${t._1}") ++ t._2)
              .mkString("\n")
          })
          .getOrElse("Couldn't analyse")
      })
      .recover(e => Console.RED + e + Console.RED)
      .get
  )
}
