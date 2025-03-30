package com.ngublag.recipes.config

import zio._

case class AppConfig(
  dbConfig: DatabaseConfig
)

object AppConfig {
  val live: ZLayer[Any, Exception, AppConfig] = ZLayer.succeed(
    AppConfig(
      dbConfig = DatabaseConfig(
        url = "jdbc:sqlite:recipes.db",
        properties = Map(
          "driver" -> "org.sqlite.JDBC"
        )
      )
    )
  )
}

case class DatabaseConfig(
  url: String,
  properties: Map[String, String]
) 