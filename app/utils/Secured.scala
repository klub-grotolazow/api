package utils

import java.util.UUID

import models.User
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.libs.json.JsValue
import play.api.mvc._


trait Secured extends Controller with MongoDatabase[User] {

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  def getAuthToken: String = UUID.randomUUID.toString

  def getExpiredDate: String = dateTimeFormatter.print(DateTime.now().plusHours(2))

  def getNewToken( username: String,
                   authToken: String,
                   expiredDate: String,
                   roles: List[String],
                   feeStatus: String
                   ): String = {
    username + "&" + authToken + "&" + expiredDate + "&" + (roles mkString ":") + "&" + feeStatus
  }

  def authorize[A](parser: BodyParser[A])(f: User => Request[A] => Result): Action[A] = Action(parser) { request =>
    request.headers.get("Authorization").map { authorizationToken =>
      authorizationToken split "&" match {
        case Array(userName, authToken) =>
          findOneByCondition("users", "auth.userName" -> userName, "auth.authToken" -> authToken).map { user =>
            val expiredDate = dateTimeFormatter.parseDateTime(user.auth.tokenExpiredDate)
            if (expiredDate.isAfterNow) {

              /** updating expiration of used token */
              update("users", user._id, user.copy(auth = user.auth.copy(tokenExpiredDate = getExpiredDate)))
              /** composing authorized action */
              f(user)(request)

            }
            else Unauthorized.withHeaders(("WWW-Authenticate", "Auth token expired"))
          }.getOrElse(NotFound)
        case _ => Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash"))
      }
    }.getOrElse(Unauthorized.withHeaders(("WWW-Authenticate", "userName&passwordHash")))
  }

}