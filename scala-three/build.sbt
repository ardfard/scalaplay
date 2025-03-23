val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0"
    )
  )
