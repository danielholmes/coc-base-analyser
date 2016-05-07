package org.danielholmes.coc.baseanalyser

import java.time.Duration

import org.danielholmes.coc.baseanalyser.model.{TileCoordinate, WallCompartment}
import org.danielholmes.coc.baseanalyser.util.TimedInvocation

object ProfileAnalysis extends App with Services {
  if (args.length != 2) {
    throw new RuntimeException("Must provide clan code and userName arg")
  }

  val clanCode = args(0)
  val userName = args(1)

  private def formatSecs(millis: Long): String = "%.3f".format(millis / 1000.0) + "s"

  private def formatSecs(duration: Duration): String = formatSecs(duration.toMillis)

  private def header(name: String) = println("-" * 35 + s"\n# $name")

  val village = facades.getWarVillageByUserName(clanCode, userName)
  if (village.isGood) {
    header("Building blocks")
    val start = System.currentTimeMillis
    Seq(
      ("Outer Tiles", () => village.get.outerTiles),
      ("Wall Compartments", () => village.get.wallCompartments),
      ("Allowed To Drop", () => village.get.coordinatesAllowedToDropTroop),
      ("Edge Prevent Drop", () => village.get.edgeOfHitCoordinatesAllowedToDropTroop)
    ).map(titleOp => (titleOp._1.padTo(29, ' '), TimedInvocation.run(titleOp._2)))
      .map(titleRes => s"${titleRes._1} ${formatSecs(titleRes._2._2)}")
      .foreach(println(_))

    val report = villageAnalyser.analyse(village.get)
    header("Rules")
    println(
      report.map(_.results.toSeq.sortBy(_.time.toMillis).reverse)
        .map(r => r.map(i => i.result.ruleDetails.shortName.padTo(29, ' ') + " " + formatSecs(i.time)))
        .map(_ ++ Set("Total: " + formatSecs(System.currentTimeMillis - start)))
        .map(_.mkString("\n"))
        .getOrElse("Couldn't analyse")
    )
  } else {
    println(village.map(_ => "").get)
  }
}
