import Dependencies._

// give the user a nice default project!
ThisBuild / organization := "net.ardfard"
ThisBuild / version      := "1.0.0"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(BuildHelper.stdSettings)
  .settings(
    name := "zio-http-play",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(
      `zio-test`,
      `zio-test-sbt`,
      `zio-http`,
      `zio-http-test`,
      `zio-macro`,
      `zio-config`,
      `zio-metrics`,
      `zio-metrics-prometheus`,
      `zio-json`,
      `zio-logging`,
      `zio-logging-slf4j`,
      logback,
      `logback-logstash`,
    ),
  )
  .settings(
    Docker / version          := version.value,
    Compile / run / mainClass := Option("net.ardfard.ziohttpplay.Ziohttpplay"),
    scalacOptions += "-Ymacro-annotations",
  )

addCommandAlias("fmt", "scalafmt; Test / scalafmt; sFix;")
addCommandAlias("fmtCheck", "scalafmtCheck; Test / scalafmtCheck; sFixCheck")
addCommandAlias("sFix", "scalafix OrganizeImports; Test / scalafix OrganizeImports")
addCommandAlias(
  "sFixCheck",
  "scalafix --check OrganizeImports; Test / scalafix --check OrganizeImports",
)
