ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "com.ardfard"


lazy val root = (project in file("."))
  .settings( name := "SbtPlay")
  .settings( libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "3.3.2",
    "org.slf4j" % "slf4j-nop" % "1.7.26",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
  ))

