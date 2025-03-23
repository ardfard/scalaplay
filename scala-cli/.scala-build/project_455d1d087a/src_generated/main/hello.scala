



object hello {
/*<script>*/                                         
                                                          
                                          
                                                

import com.spotify.scio
import scio.ContextAndArgs
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO
import ch.qos.logback.classic.Level

import org.apache.beam.sdk.options.{
  PipelineOptions,
  Description,
  PipelineOptionsFactory,
  StreamingOptions,
  ValueProvider
}
import org.apache.beam.sdk.options.Validation.Required
import org.slf4j.LoggerFactory

trait Options extends PipelineOptions with StreamingOptions {
  @Description("The Cloud Pub/Sub subscription to read from")
  @Required
  def getInputSubscription: ValueProvider[String]
  def setInputSubscription(value: ValueProvider[String]): Unit
}

case class Payload(
    item: String,
    key: Int,
    op: Char
)

private val logger = LoggerFactory
  .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
  .asInstanceOf[ch.qos.logback.classic.Logger]
logger.setLevel(Level.INFO)

def main(cmdArgs: Array[String]): Unit = {
  PipelineOptionsFactory.register(classOf[Options])
  val options = PipelineOptionsFactory
    .fromArgs(cmdArgs: _*)
    .withValidation()
    .as(classOf[Options])
  options.setStreaming(true)
  run(options)
}

def run(options: Options): Unit = {
  val sc = scio.ScioContext(options)
  logger.debug("this is debug!")
  logger.info(s"Use subscription ${options.getInputSubscription}")
  val inputIO =
    PubsubIO.readStrings().fromSubscription(options.getInputSubscription)
  sc.customInput("Input", inputIO)
    .map(s => println(s"check input ${s}"))

  sc.run()
  ()
}
/*</script>*/ /*<generated>*/
def args = hello_sc.args$
  /*</generated>*/
}

object hello_sc {
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
    hello.hashCode() // hasCode to clear scalac warning about pure expression in statement position
  }
}

