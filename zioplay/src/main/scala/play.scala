package play
import zio.console._
import zio._
import scala.concurrent.duration.Duration.Infinite

object Clocked {
  import duration._
  import zio.clock._
  val program: ZIO[Clock with Console, Nothing, Unit] = {
    putStrLn("Program started") *> zio.clock.sleep(
      500.milliseconds
    ) *> putStrLn("Stop")
  }
}

object TestSttp {
  import sttp.client._

  def run() = {
    val req =
      basicRequest
        .cookie("hello", "dangdut")
        .get("https://httpbin.org/get")
  }
}

object TestThread {
  import zio.blocking.effectBlocking
  def blockingWork(idx: Int) = {
    effectBlocking {
      println(s"Blocking work: $idx")
      Thread.sleep(1000)
    }.repeat(Schedule.forever)
  }

  def nonBlockingWork(idx: Int) = {
    import duration._
    (putStrLn(s"Non-blocking work: $idx") *> clock.sleep(1.seconds))
      .repeat(Schedule.forever)
  }

  def run() = {
    blockingWork(1).fork
  }
}

import zio.{Has, ZLayer}

object TestLayer {
  type UserRepo = Has[UserRepo.Service]
  type UserId = Int
  case class User(userId: UserId)
  final case class DBError()

  object UserRepo {
    trait Service {
      def getUser(userId: UserId): IO[DBError, Option[User]]
      def createUser(user: User): IO[DBError, Unit]
    }

    // This simple live version depends only on a DB Connection
    val inMemory: Layer[Nothing, UserRepo] = ZLayer.succeed(
      new Service {
        def getUser(userId: UserId): IO[DBError, Option[User]] = UIO(???)
        def createUser(user: User): IO[DBError, Unit] = UIO(???)
      }
    )

    //accessor methods
    def getUser(userId: UserId): ZIO[UserRepo, DBError, Option[User]] =
      ZIO.accessM(_.get.getUser(userId))

    def createUser(user: User): ZIO[UserRepo, DBError, Unit] =
      ZIO.accessM(_.get.createUser(user))
  }

  val testRepo: ZLayer[Any, Nothing, UserRepo] = ZLayer.succeed(???)
}

object Main extends zio.App {
  def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    TestThread.run().exitCode
}
