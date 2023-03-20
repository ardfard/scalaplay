//> using lib "io.getquill::quill-jdbc-zio:4.3.0"
//> using lib "org.postgresql:postgresql:42.3.1"

import zio._
import io.getquill.jdbczio.Quill
import io.getquill._
import java.nio.charset.StandardCharsets

object Main extends ZIOAppDefault:

  val dataserviceLive = ZLayer.fromFunction(DataService.apply _)
  val applicationLive = ZLayer.fromFunction(ApplicationLive.apply _)
  val datasourceLive = Quill.DataSource.fromPrefix("testDB")
  val postgresLive = Quill.Postgres.fromNamingStrategy(SnakeCase)

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    (for
      streamFQDN <- Application.getEventstreamByFQDN(
        "transactions/258334",
        "partners/ladang"
      )
      _ <- Console.printLine(
        String(streamFQDN.head.payload, StandardCharsets.UTF_8)
      )
      str <- ZIO.serviceWithZIO[ApplicationLive](_.printing())
      _ <- Console.printLine(str)
    yield ())
      .provide(dataserviceLive, applicationLive, datasourceLive, postgresLive)
