package net.ardfard.ziohttpplay

import zhttp.http._
import zhttp.service.Server
import zio._
import zio.logging.backend.SLF4J
import zio.metrics._
import zio.metrics.prometheus2._
import zio.metrics.prometheus.helpers._
import java.util.concurrent.TimeUnit
import net.ardfard.ziohttpplay.services.Logger
import net.ardfard.ziohttpplay.services.Gitlab
import net.ardfard.ziohttpplay.services.PipelinePayload
import net.ardfard.ziohttpplay.services.GitlabLive
import zio.logging.LogFormat
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

object Ziohttpplay extends ZIOAppDefault {
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "text" => Response.text("Hello World!")
    case Method.GET -> !! / "json" => Response.json("""{"greetings": "Hello World!"}""")
  }

  val appZIO = Http.collectZIO[Request] {
    case Method.GET -> !! / "time" =>
      Clock.currentDateTime.flatMap(t => ZIO.succeed(Response.text(s"${t}")))
  }

  def mid = Middleware.interceptZIOPatch(_ => Clock.nanoTime) {
    case (_, start) =>
      (for {
        finish <- Clock.nanoTime
        _      <- Console.printLine(s"duration: ${finish - start} ns")
      } yield Patch.empty).mapError(Some(_))
  }

  def mid2 = Middleware.interceptZIO((req: Request) =>
    Console.printLine(s"Request: ${req}").mapError(Some(_)),
  ) { (resp: Response, _) =>
    Console.printLine(s"Response: ${resp}").mapError(Some(_)) *> ZIO.succeed(resp)
  }

  val logFormat    = LogFormat.default
  val loggingLayer = SLF4J.slf4j(LogLevel.Debug, logFormat)

  def trying: ZIO
  // Run it like any simple app
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = (for {
    _ <- ZIO.logAnnotate(LogAnnotation("method", "GET"), LogAnnotation("status", "200"))(
      ZIO.log("Hello !"),
    )
    _ <- ZIO.log("tutung")
  } yield ()).provide(zio.logging.removeDefaultLoggers, loggingLayer).exitCode
}

