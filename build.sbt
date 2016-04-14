name := "coc-base-analyser"

version := "0.1"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(SbtWeb).enablePlugins(TomcatPlugin)

libraryDependencies ++= {
  val akkaV = "2.4.3"
  val sprayV = "1.3.3"
  Seq(
    "org.apache.commons"  %  "commons-math3" % "3.6.1",
    "io.spray"            %%  "spray-json"    % "1.3.2",

    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-servlet" % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-client"  % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,

    "org.scalactic" %% "scalactic" % "3.0.0-M15",

    "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

    "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
    "com.softwaremill.macwire" %% "util" % "2.2.2",
    "com.softwaremill.macwire" %% "proxy" % "2.2.2",

    "org.scalatest"       % "scalatest_2.11" % "2.2.6" % "test"
  )
}

/*
webappPostProcess := {
  webappDir: File => Unit;

    def listFiles(level: Int)(f: File): Unit = {
      val indent = ((1 until level) map { _ => "  " }).mkString
      if (f.isDirectory) {
        streams.value.log.info(indent + f.getName + "/")
        f.listFiles foreach { listFiles(level + 1) }
      } else streams.value.log.info(indent + f.getName)
    }
    listFiles(1)(webappDir)
}

pipelineStages in Assets := Seq(concat)
// Doesnt work but need something like this so can copy the staged files in the PostProcess above
packageBin <<= packageBin.dependsOn(WebKeys.stage)

Concat.groups := Seq(
  "js/script-group.js" -> group(Seq("js/script1.js", "js/script2.js"))
)*/
