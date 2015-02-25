name := """combos"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "be.cafeba" %% "play-cors" % "1.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "org.scalanlp" %% "breeze" % "0.10",
  "org.scalanlp" %% "breeze-natives" % "0.10"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-target:jvm-1.7"
)
