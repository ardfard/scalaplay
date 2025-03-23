



object testing {
/*<script>*/println("hello world!")
/*</script>*/ /*<generated>*/
def args = testing_sc.args$
  /*</generated>*/
}

object testing_sc {
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
    testing.hashCode() // hasCode to clear scalac warning about pure expression in statement position
  }
}

