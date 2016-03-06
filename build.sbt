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

    "org.scalatest"       % "scalatest_2.11" % "2.2.6" % "test"
  )
}

tomcat()
