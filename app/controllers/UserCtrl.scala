package controllers

import java.lang.Long
import java.text.SimpleDateFormat

import com.mongodb.{BasicDBObject, BasicDBList, DBObject}
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import models.User
import org.bson.types.ObjectId
import org.json4s._
import play.api.libs.iteratee.{Enumeratee, Iteratee, Done, Traversable}
import play.api.libs.iteratee.Input.{Empty, El}
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.JsonDSL._
import org.json4s.jackson.{Serialization, JsonMethods}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import play.api.libs.json.Json
import play.api.mvc.BodyParsers.parse._
import play.api.mvc.Results._
import play.api.mvc._
import play.api.Play
import utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

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

  implicit val formats = DefaultFormats

  val jsonParser = new Json4SParser()
  val modelParser = new ModelParser[User](Set("id"))

  val jsonHeader = ("Content-Type", "application/json")

  def create() = Action.async(modelParser) { implicit request =>
    Future {
      //      implicit val formats = Serialization.formats(NoTypeHints)

      //      val body = newDocument(request.body)
      val jsonBody = Extraction.decompose(request.body)
      val obj = idTo_id(jsonToDBObject(jsonBody).asInstanceOf[DBObject])
      if (!obj.contains("_id")) obj.put("_id", new ObjectId)
      collection.insert(obj)
      prep(obj)

      //      dbObjectToJson(removeFlag(idToId(object)))
    }.map {
      inserted => Created(write(inserted)).withHeaders(jsonHeader)
    }.recover(errorRecovery)

  }

  def idTo_id(obj: DBObject) = {
    if (obj.contains("id")) {

      val unId = obj.get("id").toString

      val checkId = Try(new ObjectId(unId)) match {
        case Success(objId) => objId
        case Failure(_) => unId
      }
      obj.put("_id", checkId)
      obj.remove("id")
    }
    obj
  }

  def prep = _idToId _ andThen removeFlag andThen obj2js

  def _idToId(obj: DBObject) = {
    if (obj.contains("_id")) {
      obj.put("id", obj.get("_id").toString)
      obj.remove("_id")
    }
    obj
  }

  def removeFlag(obj: DBObject) = {

    if (obj.contains("deleted")) {
      obj.remove("deleted")
    }
    obj
  }

  def obj2js(obj: Object): JValue = {
    import scala.collection.convert.WrapAsScala._
    import scala.language.postfixOps

    val formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS")

    obj match {
      case a: BasicDBList => JArray(a.toList.map {
        f => obj2js(f.asInstanceOf[Object])
      })
      case dbObj: BasicDBObject =>
        val javaMap = dbObj.toMap.asInstanceOf[java.util.Map[String, Object]]
        JObject(javaMap.map {
          f => JField(f._1, obj2js(f._2))
        }.toSeq: _*)
      case objId: ObjectId => JString(objId.toString)
      case s: java.lang.String => JString(s)
      case b: java.lang.Boolean => JBool(b)
      case i: java.lang.Integer => JInt(BigInt(i))
      case l: java.lang.Long => JInt(BigInt(l))
      case d: java.lang.Double => JDouble(d)
      case d: java.util.Date => JString(formatter.format(d))
      case null => JNull
      case unsupported =>
        throw new UnsupportedOperationException("Deserialising " + unsupported.getClass + ": " + unsupported)
    }
  }

  def newDocument(body: JValue): JValue = {
    val now = System.currentTimeMillis()
    val id = new ObjectId()
    body merge (("createdAt" -> now) ~ ("id" -> id.toString))
  }

  def jsonToDBObject(jsonValue: JValue): Object = {
    import scala.collection.convert.WrapAsJava._
    val formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS")

    jsonValue match {
      case JString(s) => s/*Try {
        formatter.parse(s)
      } match {
        case Success(date) => date
        case Failure(_) => s
      }*/
      case JInt(n) => new Long(n.toLong)
      case JDecimal(n) => new java.lang.Double(n.toDouble)
      case JDouble(n) => new java.lang.Double(n)
      case JNull => null
      case JNothing => null
      case JBool(b) => Boolean.box(b)
      case a: JArray =>
        val list = new BasicDBList()
        list.addAll(a.arr.map(f => jsonToDBObject(f)))
        list
      case o: JObject =>
        println("OBJECT " + o)
        val fields = o.obj
        new BasicDBObject(fields.map(f => (f._1, jsonToDBObject(f._2))).toMap)
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
      Traversable.takeUpTo[Array[Byte]](DEFAULT_MAX_TEXT_LENGTH).apply(
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

  /** Parsing request to concrete case class. It needs a JsonReader implicit */
  class ModelParser[A](forbidden: Set[String])(implicit manifest: Manifest[A], formats: Formats) extends BodyParser[A] {

    def apply(request: RequestHeader): Iteratee[Array[Byte], Either[Result, A]] = {
      Traversable.takeUpTo[Array[Byte]](DEFAULT_MAX_TEXT_LENGTH).apply(
        Iteratee.consume[Array[Byte]]().map {bytes =>
          scala.util.control.Exception.allCatch[A].either {
            /** change bytes to string if possible and then to case class */
            val ast = JsonMethods.parse(new String(bytes, request.charset.getOrElse("utf-8")))

            /** remove fields that should be generated by default */
            if (forbidden.nonEmpty) {
              ast.removeField {
                case (a, v) if forbidden.contains(a) => true
                case (_, _) => false
              }.extract[A]
            } else {
              ast.extract[A]
            }
          }.left.map {
            /** Exception from json4s */
            case MappingException(msg, cause) =>

              /** remove \n aand replace it with space. */
              val fm = msg.replace("\n", ". ")
              (Play.maybeApplication.map(app => BadRequest(DeserializationError(Some(JString(fm))).json).withHeaders("Content-Type" -> "application/json"))
                .getOrElse(Results.BadRequest), bytes)

            /** Some other unexpected exception */
            case other =>
              (Play.maybeApplication.map(app => BadRequest(ServerError(Some(JString(other.toString))).json).withHeaders("Content-Type" -> "application/json"))
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
}


