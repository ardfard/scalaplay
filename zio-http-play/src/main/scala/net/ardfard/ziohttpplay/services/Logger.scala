package net.ardfard.ziohttpplay.services

import zio._
import zio.macros.accessible

@accessible
trait Logger {
  def log(message: String): Task[Unit]
}

class LoggerLive(console: zio.Console) extends Logger {
  def log(message: String): Task[Unit] = console.print(message)
}

object LoggerLive {
  def layer = ZLayer(ZIO.console.flatMap(c => ZIO.succeed(new LoggerLive(c))))
}
