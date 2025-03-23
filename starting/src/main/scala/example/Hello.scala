package example
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import scala.concurrent.ExecutionContext.global

import cats.effect._
import cats.syntax.all._
import org.http4s.dsl._
import org.http4s._
import org.http4s.implicits._

object BlazeExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    BlazeExampleApp.resource[IO].use(_ => IO.never).as(ExitCode.Success)
}

object BlazeExampleApp {
  def httpApp[F[_]: Async]: HttpApp[F] =
    Router(
      "/http4s" -> (new Service[F]).routes
    ).orNotFound

  def resource[F[_]: Async]: Resource[F, Server] = {
    val app = httpApp[F]
    BlazeServerBuilder[F](global)
      .bindHttp(8080)
      .withHttpApp(app)
      .resource
  }

}

class Service[F[_]](implicit F: Async[F]) extends Http4sDsl[F] {
  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> root =>
      Ok("success")
    }
}
