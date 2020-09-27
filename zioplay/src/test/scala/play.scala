import zio.test._
import zio.clock.nanoTime
import Assertion._

object PlayTest extends DefaultRunnableSpec {
  val suite1 = suite("suite1") {
    testM("s1.t1")(assertM(nanoTime)(isGreaterThanEqualTo(0L)))

  }
  def spec = suite("All test")(suite1)
}
