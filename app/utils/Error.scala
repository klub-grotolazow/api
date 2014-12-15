package utils

import org.json4s._
import org.json4s.jackson.Serialization._

/**
 * Created by michal on 09.12.14.
 */

sealed abstract class Error(val code: Int, val message: String, val data: Option[JValue]) extends Exception {

  implicit val formats = DefaultFormats

  def json = write(this)

  val response = code / 100
}

/** BAD REQUEST - 400 */
case class DeserializationError(override val data: Option[JValue]) extends Error(40001, "Deserialization error", data)


case class ValidationError(override val data: Option[JValue]) extends Error(40002, "Validation error", data)


case object LeaderboardError extends Error(40003, "Leaderboard error", None)

case object SubresourceError extends Error(40005, "Subresource error", None)

case object ExpressionParseError extends Error(40006, "Error occurred during parsing an expression", None)


case class BadSyntaxError(override val data: Option[JValue] = None) extends Error(40007, "Bad syntax error", data)

case object DBQueryError extends Error(40008, "Database query error occurred", None)

case object UnrecognizedPropertyError extends Error(40009, "Unrecognised property error", None)

case object ConstraintViolationError extends Error(40010, "Constraint violation error", None)

case object InvalidJsonError extends Error(40011, "Invalid JSON error", None)


case class BadRequestError(override val data: Option[JValue] = None) extends Error(40012, "Bad request error", data)

case object QueryTimeoutError extends Error(40014, "Query execution timeout has been reached", None)

case object PaginatorPagesError extends Error(40013, "Page is greater than number of pages", None)

/** Stripe errors */
case class StripeInvalidRequest(msg: String) extends Error(40015, msg, None)


case class StripeCardError(msg: String = "Stripe card error occurred") extends Error(40016, msg, None)


case class StripeError(msg: String = "Stripe error occurred") extends Error(40017, msg, None)


case class LogoutError(msg: String = "Logout error occurred") extends Error(40018, msg, None)


case class AuthError(msg: String) extends Error(40019, msg, None)

/** FORBIDDEN - 401 */
case class AuthenticationError(msg: String) extends Error(40101, msg, None)


case object NotAnOwnerError extends Error(40102, "You must be an owner to access this action", None)

/** PAYMENT REQUIRED - 402 */
case object UserLimitReached extends Error(40201, "User limit has been reached", None)

case object LeaderboardLimitReached extends Error(40202, "Leaderboard limit has been reached", None)

case object StorageLimitReached extends Error(40203, "Storage limit has been reached", None)

case object GamificationLimitReached extends Error(40204, "Gamification limit has been reached", None)

/** NOT FOUND - 404 */
case object NotFoundError extends Error(40401, "Element not found", None)

case object ResourceError extends Error(40402, "This resource does not exist, look into the documentation", None)

/** Server error - 500 */
case class ServerError(override val data: Option[JValue]) extends Error(50001, "Unexpected error occurred", data)

/** Server error - 503 */
case object OauthTimeoutError extends Error(50301, "Authorization timeout, please try again after a while", None)