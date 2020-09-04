name := """play-ground"""
organization := "com.ardfard"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.13.3"

resolvers += "Atlassian's Maven Public Repository" at "https://packages.atlassian.com/maven-public/"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"
)
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test"
)
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11"
libraryDependencies += "com.iheart" %% "ficus" % "1.5.0"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

// sourceGenerators in Compile += slick.taskValue

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.ardfard.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.ardfard.binders._"

// code generation task
// lazy val slick = taskKey[Seq[File]]("gen-tables")

/*
slick := {
  val dir = (sourceManaged in Compile) value
  val outputDir = dir / "slick"
  val url = "postgres://ardfard@127.0.0.1/playground"
  val jdbcDriver = "org.h2.Driver"
  val slickDriver = "slick.jdbc.H2Profile"
  val pkg = "demo"

  val cp = (dependencyClasspath in Compile) value
  val s = streams value

  runner.value
    .run(
      "slick.codegen.SourceCodeGenerator",
      cp.files,
      Array(slickDriver, jdbcDri
  val file = outputDir / pkg / "Tables.scala"

  Seq(file)
}*/
