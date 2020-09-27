import scala.collection.IndexedSeqView.Concat
import zio.clock.Clock
import zio._
import sttp.client.quick._
import zio.clock._
import zio.console._

object Play {
  import duration._
  val program: ZIO[Clock with Console, Nothing, Unit] = {
    putStrLn("Program started") *> sleep(500.milliseconds) *> putStrLn("Stop")
  }
}

Runtime.default.unsafeRun(Play.program)
