name := """appman"""
organization := "com.appman"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion in ThisBuild := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.postgresql" % "postgresql" % "42.2.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.appman.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.appman.binders._"
