



object SideInput {
/*<script>*/                                         
                                                          
                                          

import com.spotify.scio._
import com.spotify.scio.values.SideOutput

private val logger = LoggerFactory
  .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
  .asInstanceOf[ch.qos.logback.classic.Logger]
logger.setLevel(Level.INFO)

def main(cmdLineArgs: Array[String]): Unit = {
  val (sc, args) = ContextAndArgs(cmdLineArgs)

  val stopWords = args.optional("stopWords") match {
    case Some(value) => sc.textFile(value)
    case None        => sc.parallelize(Seq("a", "an", "the", "of", "and", "or"))
  }

  val sideIn = stopWords.asSetSingletonSideInput

  val wordCount = sc
    .textFile(args.getOrElse("input", ".scalafmt.conf"))
    .withSideInputs(sideIn)
    .flatMap { (line, ctx) =>
      val stop = ctx(sideIn)
      line
        .split("[^a-zA-Z']+")
        .filter(_.nonEmpty)
        .map(_.toLowerCase())
        .filter(!stop.contains(_))
    }
    .toSCollection
    .countByValue

  val oneLetter = SideOutput[(String, Long)]()
  val twoLetter = SideOutput[(String, Long)]()
  val threeLetter = SideOutput[(String, Long)]()

  val (fourOrMoreLetter, sideOutputs) = wordCount
    .withSideOutputs(oneLetter, twoLetter, threeLetter)
    .flatMap { case ((word, count), ctx) =>
      word.length() match {
        case 1 => ctx.output(oneLetter, (word, count))
        case 2 => ctx.output(twoLetter, (word, count))
        case 3 => ctx.output(threeLetter, (word, count))
        case _ =>
      }
      if (word.length() >= 4) Some((word, count)) else None
    }

  def toString(kv: (String, Long)) = kv._1 + ": " + kv._2

  fourOrMoreLetter.map(toString).saveAsTextFile("output4")
  sideOutputs(oneLetter).saveAsTextFile("output1")
  sideOutputs(twoLetter).saveAsTextFile("output2")
  sideOutputs(threeLetter).saveAsTextFile("output3")

  sc.run()
  ()
}
/*</script>*/ /*<generated>*/
def args = SideInput_sc.args$
  /*</generated>*/
}

object SideInput_sc {
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
    SideInput.hashCode() // hasCode to clear scalac warning about pure expression in statement position
  }
}

