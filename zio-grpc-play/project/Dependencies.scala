import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val ziogrpc = "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-core" % "0.5.0"
  lazy val ziogrpcCodegen = "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.5.0"
}
