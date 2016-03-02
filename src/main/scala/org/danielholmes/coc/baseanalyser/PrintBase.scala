package org.danielholmes.coc.baseanalyser

import org.danielholmes.coc.baseanalyser.apigatherer.BaseGatherer
import org.danielholmes.coc.baseanalyser.baseparser.{HardCodedElementFactory, VillageJsonParser}
import org.danielholmes.coc.baseanalyser.stringdisplay.StringDisplayer

object PrintBase {
  def main(args: Array[String]) = {
    if (args.length != 1) {
      throw new RuntimeException("Must provide userName arg")
    }

    val parser = new VillageJsonParser(new HardCodedElementFactory())
    val printer = new StringDisplayer
    val gatherer = new BaseGatherer

    val json = gatherer.gather(args(0))
    val base = parser.parse(json)
    System.out.println(printer.build(base))
  }
}
