package com.ngublag.recipes

import zio.json._
import io.getquill._
import io.getquill.context.Context

case class Recipe(
  id: Option[Long],
  title: String,
  making_time: String,
  serves: String,
  ingredients: String,
  cost: Int,
  created_at: Option[String],
  updated_at: Option[String]
)

object Recipe {
  implicit val recipeEncoder: JsonEncoder[Recipe] = DeriveJsonEncoder.gen[Recipe]
  implicit val recipeDecoder: JsonDecoder[Recipe] = DeriveJsonDecoder.gen[Recipe]
  
} 