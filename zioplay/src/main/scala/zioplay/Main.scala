package zioplay


object ourzio:

  final case class ZIO[E,A](thunk: () => Either[E, A]):
    def flatMap[B](azb: A => ZIO[E, B]) = 
      ZIO {() => 
        val errorOrA = thunk()
        val errorOrB = errorOrA match
          case Right(a) => azb(a)
          case Left(e) => ZIO.fail(e)
        val b = errorOrB.thunk()
        b
      }

    def map[B](ab: A => B): ZIO[E, B] = 
      ZIO { () => 
        val errorOrA = thunk()
        errorOrA match
          case Right(a) => Right(ab(a))
          case Left(e) => Left(e)
      }

  object ZIO:
    def succeed[A](a: => A): ZIO[Nothing, A] = ZIO( () => Right(a))
    def fail[E](e: => E): ZIO[E, Nothing] = ZIO( () => Left(e))

  object Runtime:
    object default:
      def runUnsafeSync[E,A](zio: ZIO[E, A]): Either[E, A] = 
        zio.thunk()

  object console:
    def putStrLn(line: => String) = ZIO.succeed(println(line))
    def getStrLn = ZIO.succeed(scala.io.StdIn.readLine)

object Main extends App:
  import ourzio.*
  val program = for {
      _ <- console.putStrLn("What is your Name ?")
      name <- console.getStrLn
      _ <- console.putStrLn("hello")
    } yield ()
  Runtime.default.runUnsafeSync(program)