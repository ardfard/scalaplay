package example

import scala.util.Try
import scala.io.StdIn.readLine

object App0 {
  def main: Unit = {
    println("what is your name?")
    val name = readLine()
    println("Hello, " + name + ", welcome to the game!")

    var exec = true

    while (exec) {
      val num = scala.util.Random.nextInt(5) + 1

      println("Dear " + name + ", please guess a number from 1 to 5:")

      val guess = readLine().toInt

      if (guess == num) println("You guessed right, " + name + "!")
      else println("You guessed wrong, " + name + "! The number was: " + num)

      println("Do you want to continue, " + name + "?")

      readLine() match {
        case "y" => exec = true
        case "n" => exec = false
      }
    }

  }
}

trait Iterator[A] {
  def hasNext: Boolean
  def next: A
}

abstract class AbsIterator {
  type T
  def hasNext: Boolean
  def next(): T
}

class IntIterator(to: Int) extends Iterator[Int] {
  private var current = 0
  override def hasNext: Boolean = current < to
  override def next: Int = {
    if (hasNext) {
      val t = current
      current += 1
      t
    } else 0
  }
}

class StringIterator(string: String) extends AbsIterator {
  type T = Char
  private var i = 0
  def hasNext: Boolean = i < string.length()
  def next() = {
    val ch = string charAt i
    i += 1
    ch
  }
}

trait RichIterator extends AbsIterator
trait Foo[C[_]] {
  def create(i: Int): C[Int]
}

object FooList extends Foo[List] {
  def create(i: Int): List[Int] = List(i)
}

object FooEitherString extends Foo[Either[String, *]] {
  def create(i: Int): Either[String, Int] = Right(i)
}

class JustTest(val c: Int) {
  def p() = println(c)
  def apply() = println(c)
}


object TerminalFP {
  def main = {

    type Id[T] = T

    object FooId extends Foo[Id] {
      def create(i: Int): Int = i
    }

    trait Terminal[C[_]] {
      def read: C[String]
      def write(t: String): C[Unit]
    }

    type Now[T] = T

    object TerminalSync extends Terminal[Now] {
      def read: String = ???
      def write(t: String): Unit = ???
    }

    FooId.create(1)
    FooList.create(4)
    FooEitherString.create(1)
  }
}


object FutureTest {
  import scala.concurrent.duration._
  import java.util.concurrent.TimeUnit._
  import scala.concurrent._
  import ExecutionContext.Implicits.global

  def main: Unit = {

    val jt = new JustTest(10)
    jt()

    val f = Future {
      Thread.sleep(1)
      42
    }
    f.foreach(println(_))
    Await.ready(f, 10 millis)
  }

}

object TestSlick {
  import slick.jdbc.H2Profile.api._
  
  def main = {

  }
}

import simulacrum._

@typeclass trait Ordering[T] {
  def compare(x: T, y: T): Int 
  @op("<") def lt(x: T, y: T): Boolean = compare(x,y) < 0
  @op(">") def gt(x: T, y: T): Boolean = compare(x,y) > 0
}

@typeclass trait Numeric[T] extends Ordering[T] {
  @op("+") def plus(x: T, y: T): T
  @op("*") def times(x: T, y: T): T
  @op("unary_-") def negate(x: T): T
  def zero: T
  def abs(x: T): T = if (lt(x, zero)) negate(x) else x
}

object Hello extends App {
  import Numeric.ops._
  def signOfTimes[T: Numeric](t:T): T = -(t.abs) * t

  implicit val NumericDouble: Numeric[Double] = new Numeric[Double] {
    def plus(x: Double, y:Double) = x + y
  }


}
