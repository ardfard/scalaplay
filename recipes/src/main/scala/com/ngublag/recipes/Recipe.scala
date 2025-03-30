package com.ngublag.recipes

import zio.json._

case class Recipe(
  id: Long,
  name: String,
  ingredients: String,
  instructions: String,
  cookingTime: Int, // in minutes
  servings: Int
)

object Recipe {
  implicit val recipeEncoder: JsonEncoder[Recipe] = DeriveJsonEncoder.gen[Recipe]
  implicit val recipeDecoder: JsonDecoder[Recipe] = DeriveJsonDecoder.gen[Recipe]
} 