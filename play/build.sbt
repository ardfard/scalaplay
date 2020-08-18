import Dependencies._

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"
ThisBuild / scalacOptions ++= Seq(
    "-language:_",
    "-Ymacro-annotations",
    "-Xfatal-warnings"
  )

lazy val root = (project in file("."))
  .settings(
    name := "play",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.scalaz"           %% "scalaz-core"    % "7.3.2",
    libraryDependencies += "org.typelevel" %% "simulacrum"     % "1.0.0",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "org.slf4j" % "slf4j-nop" % "1.7.26",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
      )
    )

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
