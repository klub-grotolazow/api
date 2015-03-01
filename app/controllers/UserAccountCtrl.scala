package controllers

import models.UserAccount
import play.api.libs.json.{JsError, Json}
import play.api.mvc.BodyParsers.parse
import play.api.mvc._
import utils.MongoDatabase

class UserAccountCtrl extends Controller with MongoDatabase[UserAccount] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[UserAccount]) = Action(parse.json) { request =>

    /** needs deserializer implicit Reads */
    request.body.validate[UserAccount].map { userAccount =>
      insert(collectionName = "user_accounts", userAccount)

      /** needs serializer implicit Writes */
      Created(Json.toJson(userAccount)).withHeaders(jsonHeader, "Location" -> s"\\user_accounts\\${userAccount._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[UserAccount]) = Action {
    val userAccounts: List[UserAccount] = find(collectionName = "user_accounts")
    Ok(Json.toJson(userAccounts)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "user_accounts", id)
      .map(userAccount => Ok(Json.toJson(userAccount)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

  def edit(id: String)(implicit manifest: Manifest[UserAccount]) = Action(parse.json) { request =>
    request.body.validate[UserAccount].map(userAccount =>
      update(collectionName = "user_accounts", id, userAccount)
        .map(userAccount => Ok(Json.toJson(userAccount)).withHeaders(jsonHeader))
        .getOrElse {
        insert(collectionName = "user_accounts", userAccount)
        Created(Json.toJson(userAccount)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete(collectionName = "user_accounts", id)
      .map(userAccount => Ok(Json.toJson(userAccount)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }
}


