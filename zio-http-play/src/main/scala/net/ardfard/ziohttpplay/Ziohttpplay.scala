package net.ardfard.ziohttpplay

import zhttp.http._
import zhttp.service.Server
import zio._
import java.util.concurrent.TimeUnit

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

  // Run it like any simple app
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    Server.start(8090, (app ++ appZIO) @@ mid @@ mid2).exitCode
}
