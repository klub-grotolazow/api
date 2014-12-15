package controllers

import com.mongodb.DBObject
import com.mongodb.casbah.MongoClient
import com.typesafe.config.ConfigFactory
import org.bson.types.ObjectId
import org.json4s._
import play.api.libs.iteratee.{Enumeratee, Iteratee, Done, Traversable}
import play.api.libs.iteratee.Input.{Empty, El}
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import play.api.mvc.Results._
import play.api.mvc._
import play.api.Play
import utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by michal on 28.11.14.
 */
class UserCtrl extends Controller {
  val collectionName = "users"
  val config = ConfigFactory.load
  val mongoDbHost = config.getString("mongodb.host")
  val mongoDbPort = config.getInt("mongodb.port")
  val dbName = config.getString("mongodb.dbname")

  val mongoClient = MongoClient(mongoDbHost, mongoDbPort)
  val db = mongoClient(dbName)
  val collection = db(collectionName)

  val jsonParser = new Json4SParser()

  implicit val formats = DefaultFormats

  def create() = Action(jsonParser) { implicit request =>
    val future = Future {
      //        Ok(request.body.extract[User])
      val body: JValue = newDocument(request.body)
//      collection.insert(jsonToDBObject(body).asInstanceOf[DBObject])
      //dbObjectToJson(removeFlag(idToId(object)))
//      body
      body
//      println(compact(JsonMethods.render(body)))

    }.map {
      inserted => Created(write(inserted)).withHeaders(
        "Content-Type" -> "application/json",
        "Location" -> (request.host + "/users/" + inserted))
    }.recover(errorRecovery)
//    Ok("test")

  }

  def newDocument(body: JValue): JValue = {
    val now = System.currentTimeMillis()
    val id = new ObjectId()
    body merge (("createdAt" -> now) ~ ("id" -> id.toString))
  }

  def jsonToDBObject(jsonValue: JValue) = {
    jsonValue match {
      case JString(s) => s
    }
  }

  /** Parsing from JSON to DBObject */
  class Json4SParser() extends BodyParser[JValue] {
    /** Either like Option, Left like Some, Right like None
      * */
    def apply(request: RequestHeader): Iteratee[Array[Byte], Either[Result, JValue]] = {
      /** Iteratee - pipeline paradigm, processing sequentially presented chunks of input data
        * input is Array[Byte], output is Either[Result, JValue]
        * involve Futures so that asynchronous processing can be performed */
      Traversable.takeUpTo[Array[Byte]](100).apply(
        Iteratee.consume[Array[Byte]]().map {bytes =>
          scala.util.control.Exception.allCatch[JValue].either {
              JsonMethods.parse(new String(bytes, request.charset.getOrElse("utf-8")))
          }.left.map {
            case e: Error =>
              (Play.maybeApplication.map(app =>
                BadRequest(ValidationError(e.data).json).withHeaders("Content-Type" -> "application/json"))
                .getOrElse(Results.BadRequest), bytes)
          }
        }).flatMap(Iteratee.eofOrElse(Results.EntityTooLarge)
        ).flatMap {
        case Left(b) => Done(Left(b), Empty)
        case Right(it) => it.flatMap {
          case Left((r, in)) => Done(Left(r), El(in))
          case Right(json) => Done(Right(json), Empty)
        }
      }
    }
  }

  /** Changing from DBObject to JSON */

  val errorRecovery: PartialFunction[Throwable, Result] = {
    case a: Error => Status(a.response)(a.json).withHeaders(("Content-Type", "application/json"))
    case other: Throwable =>
      println(other.printStackTrace())
      InternalServerError(ServerError(Some(other.getMessage)).json).withHeaders(("Content-Type", "application/json"))
  }
}


