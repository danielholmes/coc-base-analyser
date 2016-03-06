name := "coc-base-analyser"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "org.apache.commons"  % "commons-math3" % "3.6",
    "io.spray"            %%  "spray-json"    % "1.3.2",

    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-servlet" % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-client" % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,

    "com.github.spullara.mustache.java" % "compiler" % "0.9.1",

    "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
    "com.softwaremill.macwire" %% "util" % "2.2.2",
    "com.softwaremill.macwire" %% "proxy" % "2.2.2",

    "org.scalatest"       % "scalatest_2.11" % "2.2.6" % "test"
  )
}

tomcat()
