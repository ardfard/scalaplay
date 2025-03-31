package com.ngublag.recipes

import zio._
import javax.sql.DataSource
import io.getquill._
import io.getquill.jdbczio.Quill
import io.getquill.SqliteZioJdbcContext
import com.ngublag.recipes.Recipe
import com.ngublag.recipes.db.Database

trait RecipeService {
  def getAll: ZIO[Any, Exception, List[Recipe]]
  def getById(id: Long): ZIO[Any, Exception, Option[Recipe]]
  def create(recipe: Recipe): ZIO[Any, Exception, Long]
  def update(id: Long, recipe: Recipe): ZIO[Any, Exception, Boolean]
  def delete(id: Long): ZIO[Any, Exception, Boolean]
}

object RecipeService {
  def getAll: ZIO[RecipeService, Exception, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getAll)

  def getById(id: Long): ZIO[RecipeService, Exception, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getById(id))

  def create(recipe: Recipe): ZIO[RecipeService, Exception, Long] =
    ZIO.serviceWithZIO[RecipeService](_.create(recipe))

  def update(id: Long, recipe: Recipe): ZIO[RecipeService, Exception, Boolean] =
    ZIO.serviceWithZIO[RecipeService](_.update(id, recipe))

  def delete(id: Long): ZIO[RecipeService, Exception, Boolean] =
    ZIO.serviceWithZIO[RecipeService](_.delete(id))
}

case class RecipeServiceLive(ds: DataSource) extends RecipeService {
  private val ctx = new SqliteZioJdbcContext(SnakeCase)

  import ctx._

  // Define the table name
  private val recipes = quote {
    querySchema[Recipe]("recipes")
  }

  override def getAll: ZIO[Any, Exception, List[Recipe]] = {
    for {
      _ <- ZIO.logDebug("Fetching all recipes")
      recipes <- ctx.run {
        quote {
          recipes.map(r => r)
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
          recipes.filter(_.id == lift(Some(id): Option[Long]))
        }
      }
      _ <- ZIO.logDebug(s"Recipe ${if (recipe.headOption.isDefined) "found" else "not found"}")
    } yield recipe.headOption
  }.provideLayer(ZLayer.succeed(ds))

  override def create(recipe: Recipe): ZIO[Any, Exception, Long] = {
    for {
      _ <- ZIO.logInfo(s"Creating new recipe: ${recipe.title}")
      id <- ctx.run {
        quote {
          recipes.insertValue(lift(recipe))
        }
      }
      _ <- ZIO.logInfo(s"Created recipe with id: $id")
    } yield id
  }.provideLayer(ZLayer.succeed(ds))

  override def update(id: Long, recipe: Recipe): ZIO[Any, Exception, Boolean] = {
    for {
      _ <- ZIO.logInfo(s"Updating recipe with id: $id")
      updated <- ctx.run {
        quote {
          recipes.filter(_.id.getOrElse(0) == lift(id)).updateValue(lift(recipe))
        }
      }
      _ <- ZIO.logInfo(s"Recipe ${if (updated == 1) "updated" else "not found"}")
    } yield updated == 1
  }.provideLayer(ZLayer.succeed(ds))

  override def delete(id: Long): ZIO[Any, Exception, Boolean] = {
    for {
      _ <- ZIO.logInfo(s"Deleting recipe with id: $id")
      deleted <- ctx.run {
        quote {
          recipes.filter(_.id.getOrElse(0) == lift(id)).delete
        }
      }
      _ <- ZIO.logInfo(s"Recipe ${if (deleted == 1) "deleted" else "not found"}")
    } yield deleted == 1
  }.provideLayer(ZLayer.succeed(ds))
}

object RecipeServiceLive {
  val layer: ZLayer[DataSource, Nothing, RecipeService] =
    ZLayer.fromFunction(RecipeServiceLive.apply _)
} 