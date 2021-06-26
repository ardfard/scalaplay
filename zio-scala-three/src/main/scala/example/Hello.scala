package example

import zio.console._
import zio.random._
import zio.clock._
import zio.duration._

def sleepRandom(from: String) = 
  for 
    x <- nextIntBetween(100, 200)
    _ <- putStrLn(s"${from}: Starting sleep for ${x}ms")
    _ <- sleep(x.milliseconds)
    _ <- putStrLn(s"${from}: Waking up, slept for ${x}ms")
  yield ()

def doWork =
  for 
    _ <- putStrLn("Start working.")
    _ <- sleepRandom("doWork")
    _ <- putStrLn("End working.")
  yield ()

def program = 
  (for 
    _ <- sleepRandom("main")
    _ <- putStrLn("Main sleep complete.")
  yield ()).disconnect.race(doWork.disconnect)

@main def main = 
  zio.Runtime.default.unsafeRun(program)