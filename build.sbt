name := "coc-base-analyser"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.apache.commons" % "commons-math3" % "3.6",

  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)