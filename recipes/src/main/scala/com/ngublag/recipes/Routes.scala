package com.ngublag.recipes

import zio._
import zio.http._
import zio.json._

object RecipeRoutes {
  val routes: Routes[RecipeService, Response] = Routes(
    Method.GET / "recipes" -> handler { (req: Request) =>
      for {
        recipes <- RecipeService.getAll
      } yield Response.json(recipes.toJson)
    },
    Method.GET / "recipes" / long("id") -> handler { (id: Long, req: Request) =>
      for {
        recipe <- RecipeService.getById(id)
      } yield recipe match {
        case Some(r) => Response.json(r.toJson)
        case None => Response.notFound(s"Recipe with id $id not found")
      }
    },
    Method.PATCH / "recipes" / long("id") -> handler { (id: Long, req: Request) =>
      for {
        body <- req.body.asString
        recipe <- ZIO.fromEither(body.fromJson[Recipe]).mapError(err => 
          Response.badRequest(s"Invalid recipe format: $err")
        )
        updatedRecipe <- RecipeService.update(id, recipe)
      } yield updatedRecipe match {
        case Some(r) => Response.json(r.toJson)
        case None => Response.notFound(s"Recipe with id $id not found")
      }
    },
    Method.DELETE / "recipes" / long("id") -> handler { (id: Long, req: Request) =>
      for {
        deleted <- RecipeService.delete(id)
      } yield if (deleted) {
        Response.ok
      } else {
        Response.notFound(s"Recipe with id $id not found")
      }
    },
    Method.POST / "recipes" -> handler { (req: Request) =>
      for {
        body <- req.body.asString
        recipe <- ZIO.fromEither(body.fromJson[Recipe]).mapError(err => 
          Response.badRequest(s"Invalid recipe format: $err")
        )
        id <- RecipeService.create(recipe)
      } yield Response.json(s"""{"id": $id}""")
    }
  )
} 