import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "starting",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % "0.23.0-M1",
      "org.http4s" %% "http4s-circe" % "0.23.0-M1",
      "org.http4s" %% "http4s-blaze-server" % "0.23.0-M1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
