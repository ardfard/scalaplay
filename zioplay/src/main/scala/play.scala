package play
import zio.console._
import zio._

trait DatabaseProvider {
  def databaseProvider: DatabaseProvider.Service
}

trait Test

object DatabaseProvider {
  trait Service {
    def db: UIO[Test]
  }
}

trait LiveDatabaseProvider extends DatabaseProvider {
  override val databaseProvider= new DatabaseProvider.Service {
    override val db = ZIO.effectTotal( new Test{})
  }
}

case class Cat(name: String, kittens: Seq[Cat] = Nil)

object implicits{
  implicit class SmellyCat(cat: Cat) {
    def hasKittens: Boolean = cat.kittens.size > 0
  }
}



object Play extends zio.App {

  def fib(n: Long): UIO[Long] = {
    if (n <= 1) ZIO.succeed(n)
    else fib(n - 1).zipWith(fib(n - 2))(_ + _)
  }

  val myAppLogic =
    putStrLn("hello").flatMap(_ => getStrLn).flatMap(putStrLn(_))

  def run(args: List[String]) =
    (for {
      fib100 <- fib(5).fork
      res <- fib100.join
      _ <- putStrLn(s"$res")
    } yield ()).exitCode

}
