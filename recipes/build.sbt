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
      sqliteJdbc,
      quillZio,
      munit % Test
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf"            => MergeStrategy.concat
      case "module-info.class"           => MergeStrategy.discard
      case x if x.endsWith("/module-info.class") => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    // Revolver settings
    reStart / mainClass := Some("com.ngublag.recipes.Main"),
    reStart / javaOptions ++= Seq("-Dport=10000"),
    reStart / reLogTag := "recipes"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
