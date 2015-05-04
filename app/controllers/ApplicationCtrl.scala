package controllers

import models.User
import play.api.libs.json.{Json, JsError}
import play.api.mvc._
import utils.{MongoDatabase, Secured}

class ApplicationCtrl extends Controller with MongoDatabase[User] with Secured {

  def signup() = Action(parse.json) { request =>
    request.headers.get("Authorization").map { authenticationToken =>
      authenticationToken split "&" match {
        case Array(userName, password) =>
          request.body.validate[User].map { user =>
            val authToken = getAuthToken
            val expiredDate = getExpiredDate
            val accessToken = getNewToken(userName, authToken, expiredDate, user.auth.roles, user.auth.feeStatus)
            insert("users", user.copy(auth = user.auth.copy(authToken = authToken, tokenExpiredDate = expiredDate)))
            Created(Json.toJson(user)).withHeaders(
              "Content-Type" -> "application/json", 
              "Location" -> s"\\users\\${user._id}",
              "Authorization" -> accessToken
            )
          }.recoverTotal {
            e => BadRequest("Detected error:" + JsError.toFlatJson(e))
          }
        case _ => Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash"))
      }
    }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash")))
  }
  
  def login() = Action { request =>
    request.headers.get("Authorization").map { authenticationToken =>
      println("\nAuthorization\n")
      authenticationToken split "&" match {
        case Array(userName, password) =>
          println(s"\nuserName, password: $userName, $password\n")
//          findOneByCondition("users", "auth.userName" -> userName, "auth.passwordHash" -> password).map { user =>
          findOneByCondition("users", "firstName" -> "Michal", "lastName" -> "Kijania").map { user =>
            val authToken = getAuthToken
            println("\nauthToken " + authToken + "\n")
            val expiredDate = getExpiredDate
            val accessToken = getNewToken(userName, authToken, expiredDate, user.auth.roles, user.auth.feeStatus)
//            update("users", user._id, user.copy(auth = user.auth.copy(authToken = authToken, tokenExpiredDate = expiredDate)))
            Ok.withHeaders(("Authorization", accessToken))
          }.getOrElse(NotFound)
        case _ => Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash"))
      }
    }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash")))
  }

  def logout() = authorize (parse.empty) { user => request =>
    /** setting random token, not known to anyone, to block access by token */
    update("users", user._id, user.copy(auth = user.auth.copy(authToken = getAuthToken)))
    Ok
  }
  
}