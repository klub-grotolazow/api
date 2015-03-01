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

  def find(collectionName: String)(implicit manifest: Manifest[T]): List[T] = {
    val collection = db(collectionName)

    val dbObjects = collection.find()
    /** needs deserialization from DBObject to e.g. models.User */
    dbObjects.map(dbObject => grater[T].asObject(dbObject)).toList
  }

  def findOne(collectionName: String, id: String)(implicit manifest: Manifest[T]) = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}

    dao.findOneById(id)
  }
  
  def update(collectionName: String, id: String, modelObject: T)(implicit manifest: Manifest[T]) = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}
    val dbObject = grater[T].asDBObject(modelObject)

    dao.findOneById(id).map { oldDbObject =>
      dao.update(MongoDBObject("_id" -> id), dbObject, upsert = false, multi = false)
      modelObject
    }
  }

  def delete(collectionName: String, id: String)(implicit manifest: Manifest[T]) = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}

    dao.findOneById(id).map { oldDbObject =>
      dao.removeById(id)
      oldDbObject
    }
  }
}
