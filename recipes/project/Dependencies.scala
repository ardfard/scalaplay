import sbt._

object Dependencies {
  lazy val munit = "org.scalameta" %% "munit" % "1.1.0"
  lazy val zio = "dev.zio" %% "zio" % "2.1.16"
  lazy val zioHttp = "dev.zio" %% "zio-http" % "3.2.0"
  lazy val zioJson = "dev.zio" %% "zio-json" % "0.7.39"
  lazy val sqlite = "org.xerial" % "sqlite-jdbc" % "3.49.1.0"
  lazy val zioLogging = "dev.zio" %% "zio-logging" % "2.5.0"
  lazy val zioLoggingSlf4j2 = "dev.zio" %% "zio-logging-slf4j2" % "2.5.0"
  lazy val quillZio = "io.getquill" %% "quill-jdbc-zio" % "4.8.5"
  lazy val sqliteJdbc = "org.xerial" % "sqlite-jdbc" % "3.28.0"
}
