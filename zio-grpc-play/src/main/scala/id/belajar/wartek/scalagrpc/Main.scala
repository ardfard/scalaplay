package id.belajar.wartek.scalagrpc

import scalapb.zio_grpc.ServerMain

import activities._
import io.grpc.Status
import zio._
import zio.console._
import zio.clock._
import java.util.concurrent.TimeUnit
import scalapb.zio_grpc.ServiceList

class ActivitiesService() extends ZioActivities.ZActivities[ZEnv, Any] {
    def loggedIn(request: LoggedInRequest): ZIO[ZEnv, Status, LoggedInResponse] = 
        for {
            _ <- putStrLn(s"Get an activity ${request.activity} from user ${request.user} and school ${request.school}")
            now <- currentTime(TimeUnit.SECONDS)
        } yield (LoggedInResponse(now.toInt))
}
object CollectorServer extends ServerMain {
    override def port : Int = 9000

    def services: ServiceList[ZEnv] = ServiceList.add(new ActivitiesService)
}