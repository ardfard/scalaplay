import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "zio-grpc-play",
    libraryDependencies ++= Seq( 
      scalaTest % Test, 
      ziogrpc,
      "io.grpc" % "grpc-netty" % "1.38.1",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.9" % "1.17.0-0" % "protobuf",
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.9" % "1.17.0-0"
    ),
    PB.targets in Compile := Seq(
        scalapb.gen(grpc = true) -> (sourceManaged in Compile).value / "scalapb",
        scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb"
    )
  )



// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
