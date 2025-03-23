package net.ardfard.ziohttpplay.services

import zio._
import zio.macros.accessible
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder

case class PipelinePayload(id: Int, branch: String)

case class Pipeline(id: Int)

case class Job(id: Int, pipelineId: Int)

object Job {
  implicit val decoder: JsonDecoder[Job] = DeriveJsonDecoder.gen[Job]
  implicit val encoder: JsonEncoder[Job] = DeriveJsonEncoder.gen[Job]
}

@accessible
trait Gitlab {
  def triggerPipeline(payload: PipelinePayload): Task[Unit]
  def getJobs(pipelineId: Int): Task[Iterable[Job]]
}

case class GitlabLive() extends Gitlab {
  def triggerPipeline(payload: PipelinePayload): Task[Unit] =
    Console.printLine(s"triggering ${payload}") *>
      Random.nextInt.flatMap(s => Clock.sleep(Duration.fromMillis(s + 100))) *>
      Console.printLine("Finished trigger job")

  def getJobs(pipelineId: Int): Task[Iterable[Job]] =
    ZIO.succeed(Seq(Job(1, pipelineId), Job(2, pipelineId)))
}

object GitlabLive {
  val layer = ZLayer(ZIO.succeed(new GitlabLive()))
}
