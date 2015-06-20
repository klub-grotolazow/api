package utils

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import com.typesafe.config.ConfigFactory
import play.api.Play

trait MongoConnection {
  val config = ConfigFactory.load
  val uri = MongoClientURI(config.getString("db.uri"))

  val mongoClient = MongoClient(uri)
  val db = mongoClient(config.getString("db.name"))

  /** hack to bypass Play dynamic classloader when serializing to database */
  implicit val ctx = new Context {
    val name = "Custom_Classloader"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))
}
