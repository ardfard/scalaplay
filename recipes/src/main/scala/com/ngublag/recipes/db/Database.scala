package com.ngublag.recipes.db

import zio._
import sqlite._
import com.ngublag.recipes.config.AppConfig

object Database {
  def initSchema(db: sqlite.Database): ZIO[Any, Exception, Unit] = ZIO.attempt {
    db.run(
      """
      CREATE TABLE IF NOT EXISTS recipes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        ingredients TEXT NOT NULL,
        instructions TEXT NOT NULL,
        cooking_time INTEGER NOT NULL,
        servings INTEGER NOT NULL
      )
      """
    )
  }

  def createConnection(config: AppConfig): ZIO[Any, Exception, sqlite.Database] = ZIO.attempt {
    sqlite.Database.open(config.dbConfig.url)
  }

  val layer: ZLayer[AppConfig, Exception, sqlite.Database] = ZLayer.fromZIO(
    for {
      config <- ZIO.service[AppConfig]
      db <- createConnection(config)
      _ <- initSchema(db)
    } yield db
  )
} 