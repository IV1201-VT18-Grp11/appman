import com.typesafe.sbt.web.pipeline.Pipeline

name := """appman"""
organization := "com.appman"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JDebPackaging, SystemdPlugin)

scalaVersion in ThisBuild := "2.12.4"

scalafmtOnCompile := true

val webpackGen = taskKey[Seq[File]]("Webpack generator")

webpackGen := {
  webpack.toTask("").value
  val target = WebKeys.webTarget.value / "webpack"
  target.allPaths.filter(!_.isDirectory).get
}

sourceGenerators in Assets += webpackGen.taskValue

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-report")

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.github.tminglei" %% "slick-pg" % "0.15.6",
  "org.postgresql" % "postgresql" % "42.2.0",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.59",
  "commons-codec" % "commons-codec" % "1.11",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-core" % "2.13.0" % Test,
)

// Adds additional packages into Twirl
TwirlKeys.templateImports ++= Seq(
  "controllers.Security.UserReqHeader",
  "database._",
  "views._"
)

// Adds additional packages into conf/routes
play.sbt.routes.RoutesKeys.routesImport ++= Seq(
  "database._",
  "utils.Binders._"
)

coverageExcludedPackages := "controllers\\.javascript\\..+;controllers\\.Reverse.+;router\\..router+"

debianPackageDependencies += "postgresql-9.6"
// We start it ourselves after creating the database
serviceAutostart := false
