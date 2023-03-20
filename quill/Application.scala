import io.getquill._
import io.getquill.jdbczio.Quill
import zio._
import java.sql.SQLException

case class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  inline def eventstream = quote { querySchema[EventStream]("shard_1.events") }
  inline def eventstreamByFQDN = quote { (name: String, owner: String) =>
    eventstream.filter(e => e.streamOwner == owner && e.streamName == name)
  }
}
case class EventStream(
    streamName: String,
    streamOwner: String,
    payload: Array[Byte]
)

case class ApplicationLive(dataservice: DataService):
  import dataservice.quill._
  import dataservice.quill

  def getEventstream(): ZIO[Any, SQLException, List[EventStream]] =
    quill.run(dataservice.eventstream.take(10))
  def getEventstreamByFQDN(streamName: String, owner: String) =
    quill.run(dataservice.eventstreamByFQDN(lift(streamName), lift(owner)))

  def printing() = quill.translate(dataservice.eventstream)

object Application:
  def getEventstream() = ZIO.serviceWithZIO[ApplicationLive](_.getEventstream())
  def getEventstreamByFQDN(streamName: String, owner: String) =
    ZIO.serviceWithZIO[ApplicationLive](
      _.getEventstreamByFQDN(streamName, owner)
    )
