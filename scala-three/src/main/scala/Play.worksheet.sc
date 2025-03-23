for
  x <- 1 to 100
  y <- 2 to 10
yield x + y

type LeafElem[X] = X match
  case String      => Char
  case Array[t]    => LeafElem[t]
  case Iterable[t] => LeafElem[t]
  case AnyVal      => X
def leafElem[X](x: X): LeafElem[X] = ???

extension [A](a: A) def ~>[B](b: B): (A, B) = (a, b)

extension [A, B](a: A) def ~~>(b: B): (A, B) = (a, b)

def ~~~>[A, B](a: A)(b: B): (A, B) = (a, b)

"one" ~> 1

"two" ~~> 2

import scala.annotation.targetName

@targetName("Tiefigher") case class <+>[A, B](a: A, b: B)

extension [A](a: A) def <+>[B](b: B): A <+> B = new <+>(a, b)

val ab1: Int <+> String = 1 <+> "one"

trait ToJSON[T]:
  extension (t: T) def toJSON(name: String = "", level: Int = 0): String
  protected val indent = "    "
  protected def indentation(level: Int): (String, String) =
    (indent * level, indent * (level + 1))
  protected def handleName(name: String): String =
    if name.length() > 0 then s""""$name":  """ else ""

case class Point(x: Int, y: Int)

given ToJSON[Point] with
  extension (point: Point)
    def toJSON(name: String = "", level: Int): String =
      val (outdent, indent) = indentation(level)
      s"""${handleName(name)}{
        |${indent}"x": "${point.x}",
        |${indent}"y": "${point.y}"
        |$outdent}""".stripMargin

summon[ToJSON[Point]]
Point(1, 2).toJSON("circle", 0)

def printJSON[T](x: T)(using ToJSON[T]): Unit = println(x.toJSON("tset", 0))

printJSON(Point(1, 2))
