package com.ngublag.recipes.db

import zio._
import javax.sql.DataSource
import com.ngublag.recipes.config.AppConfig
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import java.io.File
import java.sql.SQLException

object Database {
  def initSchema(ds: DataSource): ZIO[Any, Exception, Unit] = ZIO.attemptBlocking {
    try {
      val conn = ds.getConnection()
      try {
        val stmt = conn.createStatement()
        try {
          // Create table
          stmt.execute(
            """
            CREATE TABLE IF NOT EXISTS recipes (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              title TEXT NOT NULL,
              making_time TEXT NOT NULL,
              serves TEXT NOT NULL,
              ingredients TEXT NOT NULL,
              cost INTEGER NOT NULL,
              created_at TEXT,
              updated_at TEXT
            )
            """
          )

          // Check if table is empty before seeding
          val resultSet = stmt.executeQuery("SELECT COUNT(*) FROM recipes")
          val count = if (resultSet.next()) resultSet.getInt(1) else 0
          resultSet.close()

          if (count == 0) {
            // Seed data
            stmt.execute(
              """
              INSERT INTO recipes (
                id,
                title,
                making_time,
                serves,
                ingredients,
                cost,
                created_at,
                updated_at
              )
              VALUES (
                1,
                'Chicken Curry',
                '45 min',
                '4 people',
                'onion, chicken, seasoning',
                1000,
                '2016-01-10 12:10:12',
                '2016-01-10 12:10:12'
              )
              """
            )

            stmt.execute(
              """
              INSERT INTO recipes (
                id,
                title,
                making_time,
                serves,
                ingredients,
                cost,
                created_at,
                updated_at
              )
              VALUES (
                2,
                'Rice Omelette',
                '30 min',
                '2 people',
                'onion, egg, seasoning, soy sauce',
                700,
                '2016-01-11 13:10:12',
                '2016-01-11 13:10:12'
              )
              """
            )
          }
          ()
        } finally {
          stmt.close()
        }
      } finally {
        conn.close()
      }
    } catch {
      case e: SQLException => 
        throw new Exception(s"SQL Error during schema initialization: ${e.getMessage}", e)
      case e: Exception => 
        throw new Exception(s"Unexpected error during schema initialization: ${e.getMessage}", e)
    }
  }.mapError(e => new Exception(s"Schema initialization failed: ${e.getMessage}", e))

  def createConnection(config: AppConfig): ZIO[Any, Exception, DataSource] = ZIO.attempt {
    // Extract the database file path from the JDBC URL
    val dbPath = config.dbConfig.url.replace("jdbc:sqlite:", "")
    
    // Create the database file if it doesn't exist
    val dbFile = new File(dbPath)
    if (!dbFile.exists()) {
      if (!dbFile.createNewFile()) {
        throw new Exception(s"Failed to create database file at: ${dbFile.getAbsolutePath}")
      }
    }
    
    if (!dbFile.canWrite()) {
      throw new Exception(s"No write permission for database file at: ${dbFile.getAbsolutePath}")
    }
    
    val ds = new org.sqlite.SQLiteDataSource()
    ds.setUrl(config.dbConfig.url)
    
    // Test the connection
    try {
      val conn = ds.getConnection()
      conn.close()
    } catch {
      case e: SQLException =>
        throw new Exception(s"Failed to establish database connection: ${e.getMessage}", e)
    }
    
    ds
  }.mapError(e => new Exception(s"Error creating database connection: ${e.getMessage}", e))

  val layer: ZLayer[AppConfig, Exception, DataSource] = ZLayer.fromZIO(
    for {
      config <- ZIO.service[AppConfig]
      ds <- createConnection(config)
      _ <- initSchema(ds)
    } yield ds
  )
} 