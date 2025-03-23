import sbt._

object Dependencies {
  val ZioVersion        = "2.0.2"
  val ZHTTPVersion      = "2.0.0-RC11"
  val ZioJsonVersion    = "0.3.0"
  val ZioLoggingVersion = "2.0.0-RC10"

  val `zio-http`      = "io.d11" %% "zhttp" % ZHTTPVersion
  val `zio-http-test` = "io.d11" %% "zhttp" % ZHTTPVersion % Test

  val `zio-test`     = "dev.zio" %% "zio-test"     % ZioVersion % Test
  val `zio-test-sbt` = "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
  val `zio-macro`    = "dev.zio" %% "zio-macros"   % ZioVersion
  val `zio-config`   = "dev.zio" %% "zio-macros"   % ZioVersion

  val `zio-metrics`            = "dev.zio" %% "zio-metrics"            % ZioVersion
  val `zio-metrics-prometheus` = "dev.zio" %% "zio-metrics-prometheus" % ZioVersion

  val `zio-json` = "dev.zio" %% "zio-json" % ZioJsonVersion

  val logback            = "ch.qos.logback"       % "logback-classic"          % "1.2.11"
  val `logback-logstash` = "net.logstash.logback" % "logstash-logback-encoder" % "7.2"

  val `zio-logging`       = "dev.zio" %% "zio-logging"       % ZioLoggingVersion
  val `zio-logging-slf4j` = "dev.zio" %% "zio-logging-slf4j" % ZioLoggingVersion
}
