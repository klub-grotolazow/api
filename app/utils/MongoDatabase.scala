package utils

import com.mongodb.casbah.MongoClient
import com.typesafe.config.ConfigFactory
import play.api.Play
import com.novus.salat._

trait MongoDatabase[T <: AnyRef] {
  val config = ConfigFactory.load
  val mongoDbHost = config.getString("mongodb.host")
  val mongoDbPort = config.getInt("mongodb.port")
  val dbName = config.getString("mongodb.dbname")

  val mongoClient = MongoClient(mongoDbHost, mongoDbPort)
  val db = mongoClient(dbName)

  /** hack to bypass Play dynamic classloader when serializing to database */
  implicit val ctx = new Context {
    val name = "Custom_Classloader"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  def insert(collectionName: String, modelObject: T)(implicit manifest: Manifest[T]): T = {
    val collection = db(collectionName)

    /** needs conversion from models.User to DBObject, use salat library */
    val dbObject = grater[T].asDBObject(modelObject)
    collection.insert(dbObject)
    modelObject
  }
}
