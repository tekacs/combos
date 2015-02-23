name := """combos"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "be.cafeba" %% "play-cors" % "1.0"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-target:jvm-1.7"
)
