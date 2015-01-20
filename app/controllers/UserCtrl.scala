package controllers

import models.User
import play.api.libs.json._
import play.api.mvc._
import utils.MongoDatabase

class UserCtrl extends Controller with MongoDatabase[User] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[User]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[User].map {
      modelObject =>
        insert(collectionName = "users", modelObject)
        /** needs serializer implicit Writes */
        Created(Json.toJson(modelObject)).withHeaders(jsonHeader)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[User]) = Action {
    val users: List[User] = readList(collectionName = "users")
    Ok(Json.toJson(users)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    readOne(collectionName = "users", id) match {
      case Some(user) => Ok(Json.toJson(user)).withHeaders(jsonHeader)
      case None => NotFound
    }
//      .map(user => Ok(Json.toJson(user)).withHeaders(jsonHeader))
//      .getOrElse(NotFound)
  }
}
