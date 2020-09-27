// import Dependencies._

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.ngublag"
ThisBuild / organizationName := "Ngublag"

val zioVersion = "1.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "zioplay",
    //libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion % "test",
      "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
      "com.softwaremill.sttp.client" %% "core" % "2.2.8",
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.2.8"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
