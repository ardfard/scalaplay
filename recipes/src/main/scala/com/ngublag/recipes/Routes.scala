package com.ngublag.recipes

import zio._
import zio.http._
import zio.json._

case class RecipeCreationFailedResponse(message: String = "Recipe creation failed!" )
object RecipeCreationFailedResponse {
  implicit val recipeCreationFailedResponseEncoder: JsonEncoder[RecipeCreationFailedResponse] = DeriveJsonEncoder.gen[RecipeCreationFailedResponse]
}

case class RecipeCreatedResponse(recipe: List[Recipe] = List.empty, message: String = "Recipe successfully created!" )
object RecipeCreatedResponse {
  implicit val recipeCreatedResponseEncoder: JsonEncoder[RecipeCreatedResponse] = DeriveJsonEncoder.gen[RecipeCreatedResponse]
}

case class GetRecipesResponse(recipes: List[Recipe] = List.empty)
object GetRecipesResponse {
  implicit val getRecipesResponseEncoder: JsonEncoder[GetRecipesResponse] = DeriveJsonEncoder.gen[GetRecipesResponse]
}

case class GetRecipeResponse(recipe: List[Recipe], message: String)
object GetRecipeResponse {
  implicit val getRecipeResponseEncoder: JsonEncoder[GetRecipeResponse] = DeriveJsonEncoder.gen[GetRecipeResponse]
}

case class RecipeUpdateFailedResponse(message: String = "Recipe update failed!" )
object RecipeUpdateFailedResponse {
  implicit val recipeUpdateFailedResponseEncoder: JsonEncoder[RecipeUpdateFailedResponse] = DeriveJsonEncoder.gen[RecipeUpdateFailedResponse]
}

case class RecipeUpdateSuccessResponse(recipe: List[Recipe], message: String = "Recipe successfully updated!" )
object RecipeUpdateSuccessResponse {
  implicit val recipeUpdateSuccessResponseEncoder: JsonEncoder[RecipeUpdateSuccessResponse] = DeriveJsonEncoder.gen[RecipeUpdateSuccessResponse]
}

case class RecipeDeleteFailedResponse(message: String = "No recipe found" )
object RecipeDeleteFailedResponse {
  implicit val recipeDeleteFailedResponseEncoder: JsonEncoder[RecipeDeleteFailedResponse] = DeriveJsonEncoder.gen[RecipeDeleteFailedResponse]
}

case class RecipeDeleteSuccessResponse(message: String = "Recipe successfully removed!" )
object RecipeDeleteSuccessResponse {
  implicit val recipeDeleteSuccessResponseEncoder: JsonEncoder[RecipeDeleteSuccessResponse] = DeriveJsonEncoder.gen[RecipeDeleteSuccessResponse]
}



object RecipeRoutes {
  val routes: Routes[RecipeService, Response] = Routes(
    Method.GET / "recipes" -> handler { (req: Request) =>
      for {
        recipes <- RecipeService.getAll.mapError(e => Response.internalServerError(e.getMessage))
      } yield Response.json(GetRecipesResponse(recipes).toJson)
    },
    Method.GET / "recipes" / int("id") -> handler { (id: Int, req: Request) =>
      for {
        recipe <- ZIO.attempt(id.toLong).flatMap(RecipeService.getById).catchAll { _ =>
          ZIO.succeed(None)
        }
      } yield recipe match {
        case Some(r) => Response.json(GetRecipeResponse(List(r), s"Recipe details by id").toJson)
        case None => Response.notFound(s"Recipe with id $id not found")
      }
    },
    Method.PATCH / "recipes" / int("id") -> handler { (id: Int, req: Request) =>
      for {
        body <- req.body.asString.orElseFail(Response.badRequest("Invalid request body"))
        updateRecipe <- ZIO.fromEither(body.fromJson[Recipe]).mapError(err => 
          Response.json(RecipeCreationFailedResponse().toJson)
        )
        existingRecipe <- ZIO.attempt(id.toLong)
          .flatMap(RecipeService.getById)
          .flatMap(ZIO.fromOption(_))
          .mapError { _ =>
          Response.json(RecipeUpdateFailedResponse().toJson)
        }
        _ <- ZIO.logInfo(s"Updating recipe with id $id: $updateRecipe")
        val newRecipe = Recipe(
          id = Some(id),
          title = if (updateRecipe.title.nonEmpty) updateRecipe.title else existingRecipe.title,
          making_time = if (updateRecipe.making_time.nonEmpty) updateRecipe.making_time else existingRecipe.making_time,
          serves = if (updateRecipe.serves.nonEmpty) updateRecipe.serves else existingRecipe.serves,
          ingredients = if (updateRecipe.ingredients.nonEmpty) updateRecipe.ingredients else existingRecipe.ingredients,
          cost = if (updateRecipe.cost > 0) updateRecipe.cost else existingRecipe.cost,
          created_at = existingRecipe.created_at,
          updated_at = Some(java.time.LocalDateTime.now().toString)
        )
        updated <-  RecipeService.update(id, newRecipe).mapError(e => Response.json(RecipeUpdateFailedResponse("Recipe update failed!").toJson))
      } yield if (updated) {
        Response.json(RecipeUpdateSuccessResponse(List(newRecipe)).toJson)
      } else {
        Response.json(RecipeUpdateFailedResponse().toJson)
      }
    },
    Method.DELETE / "recipes" / int("id") -> handler { (id: Int, req: Request) =>
      for {
        deleted <- ZIO.attempt(id.toLong).flatMap(RecipeService.delete).catchAll { _ =>
          ZIO.succeed(false)
        }
      } yield if (deleted) {
        Response.json(RecipeDeleteSuccessResponse().toJson)
      } else {
        Response.json(RecipeDeleteFailedResponse().toJson)
      }
    },
    Method.POST / "recipes" -> handler { (req: Request) =>
      for {
        body <- req.body.asString.orElseFail(Response.badRequest("Invalid request body"))
        recipe <- ZIO.fromEither(body.fromJson[Recipe]).tapError(ZIO.logError(_)).mapError(err => 
          Response.json(RecipeCreationFailedResponse().toJson)
        )
        id <- RecipeService.create(recipe).mapError(e => Response.internalServerError(e.getMessage))
      } yield Response.json(RecipeCreatedResponse(recipe = List(recipe.copy(id = Some(id)))).toJson)
    }
  )
} 