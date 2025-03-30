import Dependencies._

ThisBuild / scalaVersion     := "2.13.16"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.ngublag"
ThisBuild / organizationName := "ngublag"

lazy val root = (project in file("."))
  .settings(
    name := "recipes",
    libraryDependencies ++= Seq(
      zio,
      zioHttp,
      zioJson,
      zioLogging,
      zioLoggingSlf4j2,
      sqliteJdbc,
      quillZio,
      munit % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
