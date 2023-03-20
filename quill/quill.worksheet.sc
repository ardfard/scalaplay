import io.getquill._

val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

import ctx._

case class Person(name: String, age: Int)

val q = quote {
  query[Person].filter(_.age > 21)
}

run(q)
