package utils

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import com.typesafe.config.ConfigFactory
import play.api.Play

trait MongoConnection {
  val config = ConfigFactory.load
  val uri = MongoClientURI(config.getString("mongodb.herokuDatabaseUri"))
//  val uri = MongoClientURI("mongodb://localhost:27017/") // localhost

  val mongoClient = MongoClient(uri)
  val db = mongoClient(uri.database.get)
//  val db = mongoClient("akg") // localhost

  /** hack to bypass Play dynamic classloader when serializing to database */
  implicit val ctx = new Context {
    val name = "Custom_Classloader"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))
}
