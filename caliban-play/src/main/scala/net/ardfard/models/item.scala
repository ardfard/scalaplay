
package net.ardfard.models

import java.util.UUID
import zio.IO
import caliban.GraphQL.graphQL
import caliban.RootResolver

sealed trait ServiceError extends Throwable

object ServiceError {
  case object NotFound extends ServiceError
}

case class ItemTag(id: Int, name: String)

case class Item(id: UUID, tags: List[ItemTag], name: String)

  trait ItemService {
    def findItem(id: UUID): IO[ServiceError, Item]

    def addItem(item: Item): IO[ServiceError, Unit]
  }

  case class FindItemArgs(id: UUID)
  case class AddItemArgs(item: Item)

  case class Queries(
    findItem: FindItemArgs => IO[ServiceError, Item],
  )

  case class Mutation(
    addItem: AddItemArgs => IO[ServiceError, Unit]
  )

object Test extends App {

  

  
}