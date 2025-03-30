package com.ngublag.recipes

import zio._
import com.ngublag.recipes.Recipe
import com.ngublag.recipes.db.Database
import io.getquill.SqliteJdbcContext
import io.getquill.jdbczio.Quill.Sqlite
import io.getquill.SqliteZioJdbcContext
import io.getquill.SnakeCase
import javax.sql.DataSource

trait RecipeService {
  def getAll: ZIO[Any, Exception, List[Recipe]]
  def getById(id: Long): ZIO[Any, Exception, Option[Recipe]]
  def create(recipe: Recipe): ZIO[Any, Exception, Long]
  def update(id: Long, recipe: Recipe): ZIO[Any, Exception, Option[Recipe]]
  def delete(id: Long): ZIO[Any, Exception, Boolean]
}

object RecipeService {
  def getAll: ZIO[RecipeService, Exception, List[Recipe]] = 
    ZIO.serviceWithZIO[RecipeService](_.getAll)
  
  def getById(id: Long): ZIO[RecipeService, Exception, Option[Recipe]] = 
    ZIO.serviceWithZIO[RecipeService](_.getById(id))
  
  def create(recipe: Recipe): ZIO[RecipeService, Exception, Long] = 
    ZIO.serviceWithZIO[RecipeService](_.create(recipe))

  def update(id: Long, recipe: Recipe): ZIO[RecipeService, Exception, Option[Recipe]] = 
    ZIO.serviceWithZIO[RecipeService](_.update(id, recipe))

  def delete(id: Long): ZIO[RecipeService, Exception, Boolean] = 
    ZIO.serviceWithZIO[RecipeService](_.delete(id))
} 

case class RecipeServiceLive(ds: DataSource) extends RecipeService {

  val ctx = new SqliteZioJdbcContext(SnakeCase)

  import ctx._

  override def getAll: ZIO[Any, Exception, List[Recipe]] = {
      for {
        _ <- ZIO.logDebug("Fetching all recipes")
        recipes <- ctx.run {
          quote {
            query[Recipe].map(r => r)
          }
        }
        
        _ <- ZIO.logDebug(s"Found ${recipes.length} recipes")
      } yield recipes
    }.provideLayer(ZLayer.succeed(ds))

  override def getById(id: Long): ZIO[Any, Exception, Option[Recipe]] = {
      for {
        _ <- ZIO.logDebug(s"Fetching recipe with id: $id")
        recipe <- ctx.run {
          quote {
            query[Recipe].filter(_.id == lift(id))
          }
        }
        _ <- ZIO.logDebug(s"Recipe ${if (recipe.headOption.isDefined) "found" else "not found"}")
      } yield recipe.headOption
    }.provideLayer(ZLayer.succeed(ds))

  override def create(recipe: Recipe): ZIO[Any, Exception, Long] = {
      for {
        _ <- ZIO.logInfo(s"Creating new recipe: ${recipe.name}")
        result <- ctx.run {
          quote {
            query[Recipe].insertValue(lift(recipe))
          }
        }
        id = result
        _ <- ZIO.logInfo(s"Created recipe with id: $id")
      } yield id
    }.provideLayer(ZLayer.succeed(ds))

  override def update(id: Long, recipe: Recipe): ZIO[Any, Exception, Option[Recipe]] = {
      for {
        _ <- ZIO.logInfo(s"Updating recipe with id: $id")
        result <- ctx.run {
          quote {
            query[Recipe].filter(_.id == lift(id)).updateValue(lift(recipe))
          }
        }
        updated = result > 0
        _ <- ZIO.logInfo(s"Recipe ${if (updated) "updated" else "not found"}")
      } yield if (updated) Some(recipe.copy(id = id)) else None
    }.provideLayer(ZLayer.succeed(ds))

  override def delete(id: Long): ZIO[Any, Exception, Boolean] = {
      for {
        _ <- ZIO.logInfo(s"Deleting recipe with id: $id")
        result <- ctx.run {
          quote {
            query[Recipe].filter(_.id == lift(id)).delete
          }
        }
        deleted = result > 0
        _ <- ZIO.logInfo(s"Recipe ${if (deleted) "deleted" else "not found"}")
      } yield deleted
    }.provideLayer(ZLayer.succeed(ds))
} 

// object RecipeServiceLive {
//   val layer: ZLayer[sqlite.Database, Nothing, RecipeService] = 
//     ZLayer.fromFunction(RecipeServiceLive.apply _)
// } 