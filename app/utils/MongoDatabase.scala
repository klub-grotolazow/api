package utils

import com.mongodb.casbah.MongoClient
import com.novus.salat.dao.SalatDAO
import com.typesafe.config.ConfigFactory
import play.api.Play
import com.novus.salat._
import com.mongodb.casbah.Imports._

import scala.util.{Success, Failure, Try}

trait MongoDatabase[T <: AnyRef] extends MongoConnection {

  class DocumentNotFoundException extends Throwable

  def insert(collectionName: String, modelObject: T)(implicit manifest: Manifest[T]): T = {
    val collection = db(collectionName)

    /** needs serialization from e.g. models.User to DBObject, use salat library */
    val dbObject = grater[T].asDBObject(modelObject)
    collection.insert(dbObject)
    modelObject
  }

  def find(collectionName: String, filter: Option[MongoDBObject] = None)(implicit manifest: Manifest[T]): List[T] = {
    val collection = db(collectionName)

    val dbObjects = filter.map(filter => collection.find(filter)).getOrElse(collection.find())
    /** needs deserialization from DBObject to e.g. models.User */
    dbObjects.map(dbObject => grater[T].asObject(dbObject)).toList
  }

  def findOne(collectionName: String, id: String)(implicit manifest: Manifest[T]): Option[T] = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}

    dao.findOneById(id)
  }
  
  def update(collectionName: String, id: String, modelObject: T)(implicit manifest: Manifest[T]): Option[T] = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}
    val dbObject = grater[T].asDBObject(modelObject)

    dao.findOneById(id).map { oldDbObject =>
      dao.update(MongoDBObject("_id" -> id), dbObject, upsert = false, multi = false)
      modelObject
    }
  }

  def delete(collectionName: String, id: String)(implicit manifest: Manifest[T]): Option[T] = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}

    dao.findOneById(id).map { oldDbObject =>
      dao.removeById(id)
      oldDbObject
    }
  }
}

class MongoCollection[T <: AnyRef] extends MongoConnection {

//  def insert(collectionName: String, modelObject: T)(implicit manifest: Manifest[T]): T = {
//    val collection = db(collectionName)
//
//    /** needs serialization from e.g. models.User to DBObject, use salat library */
//    val dbObject = grater[T].asDBObject(modelObject)
//    collection.insert(dbObject)
//    modelObject
//  }

  def find(collectionName: String, filter: Option[MongoDBObject] = None)(implicit manifest: Manifest[T]): List[T] = {
    val collection = db(collectionName)

    val dbObjects = filter.map(filter => collection.find(filter)).getOrElse(collection.find())
    /** needs deserialization from DBObject to e.g. models.User */
    dbObjects.map(dbObject => grater[T].asObject(dbObject)).toList
  }

  def findOne(collectionName: String, id: String)(implicit manifest: Manifest[T]): Option[T] = {
    val collection = db(collectionName)
    val dao = new SalatDAO[T, String](collection) {}

    dao.findOneById(id)
  }

//  def update(collectionName: String, id: String, modelObject: T)(implicit manifest: Manifest[T]) = {
//    val collection = db(collectionName)
//    val dao = new SalatDAO[T, String](collection) {}
//    val dbObject = grater[T].asDBObject(modelObject)
//
//    dao.findOneById(id).map { oldDbObject =>
//      dao.update(MongoDBObject("_id" -> id), dbObject, upsert = false, multi = false)
//      modelObject
//    }
//  }

//  def delete(collectionName: String, id: String)(implicit manifest: Manifest[T]) = {
//    val collection = db(collectionName)
//    val dao = new SalatDAO[T, String](collection) {}
//
//    dao.findOneById(id).map { oldDbObject =>
//      dao.removeById(id)
//      oldDbObject
//    }
//  }
}
