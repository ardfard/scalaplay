package play
import zio.console._
import zio._
import scala.concurrent.duration.Duration.Infinite
import java.net.URL
import zio.blocking.Blocking
import play.TestLayer.UserRepo
import zio.macros.accessible

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
    // .get("https://httpbin.org/get")

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

  @accessible
  object UserRepo {
    trait Service {
      def getUser(userId: UserId): IO[DBError, Option[User]]
      def createUser(user: User): IO[DBError, Unit]
    }

    // This simple live version depends only on a DB Connection
    val inMemory: Layer[Nothing, UserRepo] = ZLayer.fromEffect(for {
      ref <- Ref.make(Map.empty[UserId, User])
    } yield (new Service {
      def getUser(userId: UserId): IO[DBError, Option[User]] =
        for {
          m <- ref.get
        } yield m.get(userId)
      def createUser(user: User): IO[DBError, Unit] =
        ref.modify(m => ((), m.updated(user.userId, user)))

    }))
  }

  val testRepo: ZLayer[Any, Nothing, UserRepo] = ZLayer.succeed(???)
}

object Main extends zio.App {
  def getUrl(url: URL): ZIO[Blocking, Exception, String] = {
    import zio.blocking.effectBlocking

    def getUrlImpl(url: URL): String =
      scala.io.Source.fromURL(url)(scala.io.Codec.UTF8).mkString

    effectBlocking(getUrlImpl(url)).refineOrDie {
      case e: Exception => e
    }
  }

  import TestLayer._

  def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] = {
    var user = User(1)
    val created: ZIO[UserRepo, DBError, Boolean] = for {
      maybeUser <- UserRepo.getUser(user.userId)
      res <- maybeUser.fold(UserRepo.createUser(user).as(true))(_ =>
        ZIO.succeed(false)
      )
    } yield res

    val create100: ZIO[UserRepo, DBError, Unit] =
      ZIO.foreachParN_(50)(0 to 100)(userid => {
        val user = User(userid)
        UserRepo.createUser(user)
      })

    val getall: ZIO[UserRepo, DBError, List[User]] =
      ZIO
        .collectAllParN(50) {
          (0 to 100).map(UserRepo.getUser(_))
        }
        .map(_.toList.flatten)

    (for {
      users <- (create100 *> getall).provideLayer(UserRepo.inMemory)
      _ <- ZIO.foreach_(users)(u => putStrLn(s"$u"))
    } yield (putStrLn("hello"))).flatten.exitCode
  }
}
