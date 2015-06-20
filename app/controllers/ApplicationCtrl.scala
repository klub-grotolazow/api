package controllers

import models.User
import play.api.libs.json.{Json, JsError}
import play.api.mvc._
import utils.{AuthorizationAction, MongoDatabase, Secured}

class ApplicationCtrl(action: AuthorizationAction) extends Controller with MongoDatabase[User] with Secured {

  def signup() = Action(parse.json) { request =>
    request.headers.get("Authorization").map { authenticationToken =>
      authenticationToken split "&" match {
        case Array(userName, password) =>
          if (findOneByCondition("users", "auth.userName" -> userName).nonEmpty) Unauthorized.withHeaders(("WWW-Authenticate", "given userName already exists"))
          else {
            request.body.validate[User].map { user =>
              val authToken = getAuthToken
              val expiredDate = getExpiredDate
              val accessToken = getNewToken(userName, authToken, user.auth.roles, user.auth.feeStatus)
              insert("users", user.copy(auth = user.auth.copy(userName = userName, passwordHash = password, authToken = authToken, tokenExpiredDate = expiredDate)))
              Created(Json.toJson(user)).withHeaders(
                "Content-Type" -> "application/json",
                "Location" -> s"\\users\\${user._id}",
                "Authorization" -> accessToken
              )
            }.recoverTotal {
              e => BadRequest("Detected error:" + JsError.toFlatJson(e))
            }
          }
        case  _ => Unauthorized.withHeaders(("WWW-Authenticate", "userName&password"))
      }
    }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "userName&password")))
  }
  
  def login() = Action { request =>
    request.headers.get("Authorization").map { authenticationToken =>
      authenticationToken split "&" match {
        case Array(userName, password) =>
          findOneByCondition("users", "auth.userName" -> userName, "auth.passwordHash" -> password).map { user =>
            val authToken = getAuthToken
            val expiredDate = getExpiredDate
            val accessToken = getNewToken(userName, authToken, user.auth.roles, user.auth.feeStatus)
            update("users", user._id, user.copy(auth = user.auth.copy(authToken = authToken, tokenExpiredDate = expiredDate)))
            Ok.withHeaders(("Authorization", accessToken))
          }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "Password is invalid")))
        case _ => Unauthorized.withHeaders(("WWW-Authenticate", "userName&password"))
      }
    }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "userName&password")))
  }

  def logout() = action.authorize (parse.empty) { user => request =>
    /** setting random token, not known to anyone, to block access by token */
    update("users", user._id, user.copy(auth = user.auth.copy(authToken = getAuthToken)))
    Ok
  }
  
}