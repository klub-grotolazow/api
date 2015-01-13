package controllers

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import models.User
import play.api.Play
import play.api.libs.json.{Json, JsError}
import play.api.mvc._
import com.novus.salat._
import com.novus.salat.global.ctx

/**
 * Created by michal on 28.11.14.
 */
class UserCtrl extends Controller {
  val config = ConfigFactory.load
  val mongoDbHost = config.getString("mongodb.host")
  val mongoDbPort = config.getInt("mongodb.port")
  val dbName = config.getString("mongodb.dbname")
  val collectionName = "users"

  /** hack to bypass Play dynamic classloader when serializing to database */
  implicit val ctx = new Context {
    val name = "Custom_Classloader"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

//  import scala.concurrent.ExecutionContext.Implicits.global
//  val db = new MongoDriver().connection(List(mongoDbHost))(dbName)
//  val collection = db(collectionName)

  val mongoClient = MongoClient(mongoDbHost, mongoDbPort)
  val collection = mongoClient(dbName)(collectionName)

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create() = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[User].map {
      user =>
        /** needs conversion from models.User to DBObject, use salat library */
        val dbObject = grater[User].asDBObject(user)
        collection.insert(dbObject)
//        val userBack = grater[User].asObject(dbObject)
        /** needs serializer implicit Writes */
        Created(Json.toJson(user)).withHeaders(jsonHeader)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
}
