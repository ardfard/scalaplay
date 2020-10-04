import zio.test._
import zio.clock.nanoTime
import Assertion._
import zio.test.mock.mockable
import zio.test.mock.Expectation._
import play.TestLayer._

@mockable[UserRepo.Service]
object UserRepoMock

object PlayTest extends DefaultRunnableSpec {
  val suite1 = suite("suite1") {
    testM("s1.t1")(assertM(nanoTime)(isGreaterThanEqualTo(0L)))
  }

  val userSuite = suite("user") {
    val mocked = UserRepoMock.GetUser(equalTo(1), value(Some(User(1))))
    val query = UserRepo.getUser(1)
    testM("get user with ID 1") {
      assertM(query.provideLayer(mocked))(isSome(equalTo(User(1))))
    }
  }

  val clockSuite = suite("clock") {
    testM("time is non-zero") {
      assertM(nanoTime)(isGreaterThan(0L))
    }
  }

  def spec = suite("All test")(suite1, userSuite)
}
