import zio._
import zio.test._
import net.ardfard.models._
import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.console._
import java.util.UUID

case object MockService extends ItemService {
  val uuid0 = new UUID(0,0)
  val uuid1 = new UUID(0,1)
  val uuid2 = new UUID(0,2)
  val uuid3 = new UUID(0,3)

  val tag1 = ItemTag(0, "GPU")
  val tag2 = ItemTag(0, "PS5")
  val tag3 = ItemTag(0, "XSX")
  val tag4 = ItemTag(0, "gero")

  val repo: scala.collection.mutable.Map[UUID, Item] = scala.collection.mutable.Map(
    uuid0 -> Item(uuid0, List(tag1), "RTX 3080"),
    uuid1 -> Item(uuid1, List(tag2), "Playstation 5"),
    uuid2 -> Item(uuid2, List(tag3), "Xbox Series X"),
    uuid3 -> Item(uuid2, List(tag4, tag1), "Radeon"),
  )

  def findItem(id: UUID): IO[ServiceError,Item] = 
    IO.fromEither(repo.get(id).toRight(ServiceError.NotFound))
  
  def addItem(item: Item): IO[ServiceError, Unit] = UIO.succeed {
    repo.put(item.id, item); 
    ()
  }
}
object ItemSpec extends DefaultRunnableSpec {
  def spec = suite("ItemSpec") {
    testM("Queries API"){
      val itemService: ItemService = MockService
      val queries = Queries(args => itemService.findItem(args.id))
      val mutations = Mutation(args => itemService.addItem(args.item))
      val api = graphQL(RootResolver(queries))
      for {
        _ <- putStrLn(api.render)
      } yield assertTrue(true)
    }
  }
}