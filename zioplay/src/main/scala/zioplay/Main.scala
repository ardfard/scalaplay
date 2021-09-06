package zioplay


object ourzio:
  final case class ZIO[+E,A](thunk: () => Either[E, A]):
    def flatMap[E1 >: E, B](azb: A => ZIO[E1, B]) = 
      ZIO {() => 
        val errorOrA = thunk()
        val errorOrB = errorOrA.fold(ZIO.fail, azb)
        errorOrB.thunk()
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
      def unsafeRunSync[E,A](zio: => ZIO[E, A]): Either[E, A] = 
        zio.thunk()

  object console:
    def putStrLn(line: => String) = ZIO.succeed(println(line))
    def getStrLn = ZIO.succeed(scala.io.StdIn.readLine())


// object Main extends App:
//   import ourzio.*
//   val program = for {
//       _ <- console.putStrLn("What is your Name ?")
//       name <- console.getStrLn
//       _ <- console.putStrLn(s"hello, ${name}")
//       _ <- ZIO.fail(throw RuntimeException("just vibing"))
//     } yield ()
//   println(Runtime.default.unsafeRunSync(program))

trait Box[-A]:
    def set(a: A): Unit
    def value[A1 <: A]: A1

object Box:
    class BoxImpl[A](var _value: A) extends Box[A]:
        def set(a: A) = 
          _value = a
        def value[A1 <: A] = _value

    def apply[A](a: A): Box[A] = 
        BoxImpl(a)


trait Animal
trait Cat extends Animal 
trait Dog extends Animal 

@main def main = 
  val b = Box(new Cat{})
  val c = b.set(new Cat{})
  val boxAnimal: Box[Animal] = b
  boxAnimal.set(new Cat{})
  println(boxAnimal.value)
