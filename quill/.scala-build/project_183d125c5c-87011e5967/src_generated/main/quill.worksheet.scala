



object `quill.worksheet` {
/*<script>*/import io.getquill._

val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

import ctx._

case class Person(name: String, age: Int)

val q = quote {
  query[Person].filter(_.age > 21)
}

run(q)
/*</script>*/ /*<generated>*/
def args = `quill.worksheet_sc`.args$
  /*</generated>*/
}

object `quill.worksheet_sc` {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }
  def main(args: Array[String]): Unit = {
    args$set(args)
    `quill.worksheet`.hashCode() // hasCode to clear scalac warning about pure expression in statement position
  }
}

