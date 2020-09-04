// import Dependencies._

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.ngublag"
ThisBuild / organizationName := "Ngublag"

lazy val root = (project in file("."))
  .settings(
    name := "zioplay",
    //libraryDependencies += scalaTest % Test,
    libraryDependencies += "dev.zio" %% "zio-streams" % "1.0.0"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
