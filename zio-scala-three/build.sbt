import Dependencies._

ThisBuild / scalaVersion     := "3.0.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ardfard.net"
ThisBuild / organizationName := "scala-three"

lazy val root = (project in file("."))
  .settings(
    name := "scala-three",
    libraryDependencies ++= Seq(
      zio
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
