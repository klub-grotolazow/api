package utils

import com.mongodb.casbah.MongoClient
import com.novus.salat.dao.SalatDAO
import com.typesafe.config.ConfigFactory
import play.api.Play
import com.novus.salat._
import com.mongodb.casbah.Imports._

import scala.util.{Success, Failure, Try}

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

  def tryObjectId(key: String) = Try(new ObjectId(key)) match {
    case Success(objId) => objId
    case Failure(_) => key
  }

  def insert(collectionName: String, modelObject: T)(implicit manifest: Manifest[T]): T = {
    val collection = db(collectionName)

    /** needs serialization from e.g. models.User to DBObject, use salat library */
    val dbObject = grater[T].asDBObject(modelObject)
    collection.insert(dbObject)
    modelObject
  }

  def readList(collectionName: String)(implicit manifest: Manifest[T]): List[T] = {
    val collection = db(collectionName)

    val dbObjects = collection.find()
    /** needs deserialization from DBObject to e.g. models.User */
    dbObjects.map(dbObject => grater[T].asObject(dbObject)).toList
  }

  def readOne(collectionName: String, id: String)(implicit manifest: Manifest[T]) = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, ObjectId](collection) {}

    dao.findOne(MongoDBObject("_id" -> id))
  }
}
