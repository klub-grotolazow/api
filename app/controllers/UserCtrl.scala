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
    request.body.validate[User].map { user =>
        insert(collectionName = "users", user)
        /** needs serializer implicit Writes */
        Created(Json.toJson(user)).withHeaders(jsonHeader, "Location" -> s"\\users\\${user._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[User]) = Action {
    val users: List[User] = find(collectionName = "users")
    Ok(Json.toJson(users)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "users", id)
      .map(user => Ok(Json.toJson(user)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }
  
  def edit(id: String)(implicit manifest: Manifest[User]) = Action(parse.json) { request =>
    request.body.validate[User].map(user =>
      update(collectionName = "users", id, user)
        .map(user => Ok(Json.toJson(user)).withHeaders(jsonHeader))
        .getOrElse {
          insert(collectionName = "users", user)
          Created(Json.toJson(user)).withHeaders(jsonHeader)
        }
      ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
}
