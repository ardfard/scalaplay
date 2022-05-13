package net.ardfard.ziohttpplay

import zio.test._
import zio.test.Assertion._
import zhttp.http._
// object ZiohttpplaySpec extends ZIOSpecDefault {
//   override def spec: ZSpec[Environment, Failure] = suite("""ZiohttpplaySpec""")(
//     testM("200 ok") {
//       checkAllM(Gen.fromIterable(List("text", "json"))) { uri =>
//         val request = Request(Method.GET, URL(!! / uri))
//         assertM(Ziohttpplay.app(request).map(_.status))(equalTo(Status.OK))
//       }
//     },
//   )
// }
