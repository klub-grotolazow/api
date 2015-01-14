package controllers

import models.User
import play.api.libs.json._
import play.api.mvc._
import utils.MongoDatabase

abstract class CrudCtrl[T: Reads] extends Controller with MongoDatabase[T] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[T]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[T].map {
      modelObject =>
        insert(collectionName = "users", modelObject)
        /** needs serializer implicit Writes */
        Created(Json.toJson(modelObject)).withHeaders(jsonHeader)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
}

class UserCtrl extends CrudCtrl[User]
