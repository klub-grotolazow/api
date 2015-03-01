package controllers

import models.Course
import play.api.libs.json.{JsError, Json}
import play.api.mvc.BodyParsers.parse
import play.api.mvc._
import utils.MongoDatabase

class CourseCtrl extends Controller with MongoDatabase[Course] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[Course]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[Course].map { course =>
      insert(collectionName = "courses", course)
      /** needs serializer implicit Writes */
      Created(Json.toJson(course)).withHeaders(jsonHeader, "Location" -> s"\\courses\\${course._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[Course]) = Action {
    val courses: List[Course] = find(collectionName = "courses")
    Ok(Json.toJson(courses)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "courses", id)
      .map(course => Ok(Json.toJson(course)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

  def edit(id: String)(implicit manifest: Manifest[Course]) = Action(parse.json) { request =>
    request.body.validate[Course].map(course =>
      update(collectionName = "courses", id, course)
        .map(course => Ok(Json.toJson(course)).withHeaders(jsonHeader))
        .getOrElse {
        insert(collectionName = "courses", course)
        Created(Json.toJson(course)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete(collectionName = "courses", id)
      .map(course => Ok(Json.toJson(course)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

}