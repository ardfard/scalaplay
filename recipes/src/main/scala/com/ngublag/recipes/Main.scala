package com.ngublag.recipes

import zio._
import zio.http._
import com.ngublag.recipes.config.AppConfig

object Main extends zio.ZIOAppDefault {
  def run = for {
    _ <- Server.serve(RecipeRoutes.routes).provide(
      Server.default,
      AppConfig.live,
      LoggingService.live,
      RecipeRepositoryLive.layer
    )
  } yield ()
} 