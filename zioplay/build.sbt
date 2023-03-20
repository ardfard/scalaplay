import Dependencies._

ThisBuild / organization := "net.ardfard"
ThisBuild / scalaVersion := "3.0.1"

ThisBuild / scalacOptions ++=
  Seq(
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Yexplicit-nulls", // experimental (I've seen it cause issues with circe)
    "-Ykind-projector",
    "-Ysafe-init", // experimental (I've seen it cause issues with circe)
  ) ++ Seq("-rewrite", "-indent") ++ Seq("-source", "future")

lazy val `zioplay` =
  project
    .in(file("."))
    .settings(name := "zioplay")
    .settings(commonSettings)
    .settings(dependencies)

lazy val commonSettings = Seq(
  update / evictionWarningOptions := EvictionWarningOptions.empty,
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings",
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value,
)

lazy val dependencies = Seq(
  libraryDependencies ++= Seq(
    // main dependencies
    "dev.zio" %% "zio" % "2.0.0-M1",
    "dev.zio" %% "zio-streams" % "2.0.0-M1"
  ),
  // libraryDependencies ++= Seq(
    // org.scalatest.scalatest,
    // org.scalatestplus.`scalacheck-1-15`,
  // ).map(_ % Test),
)
