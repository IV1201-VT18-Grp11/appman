// sbt-webpack 1.2.1 is only released for 0.13.x, use custom port to 1.x
resolvers += Resolver.bintrayIvyRepo("teozkr", "IV1201-sbt-plugins")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")
addSbtPlugin("com.github.stonexx.sbt" % "sbt-webpack" % "1.3.0-fix-14")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
