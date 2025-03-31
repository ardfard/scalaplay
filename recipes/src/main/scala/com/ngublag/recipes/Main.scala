package com.ngublag.recipes

import zio._
import zio.http._
import com.ngublag.recipes.config.AppConfig
import com.ngublag.recipes.db.Database

object Main extends zio.ZIOAppDefault {
  def run = for {
    port <- ZIO.succeed(java.lang.System.getProperty("port", "10000").toInt)
    _ <- Server.serve(RecipeRoutes.routes).provide(
      Server.defaultWithPort(10000),
      AppConfig.live,
      Database.layer,
      RecipeServiceLive.layer
    )
  } yield ()
} 