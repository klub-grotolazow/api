package controllers

import models.DocumentTemplate
import play.api.libs.json.{JsError, Json}
import play.api.mvc.BodyParsers.parse
import play.api.mvc._
import utils.MongoDatabase

class DocumentTemplateCtrl extends Controller with MongoDatabase[DocumentTemplate] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[DocumentTemplate]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[DocumentTemplate].map { doc =>
      insert(collectionName = "document_templates", doc)
      /** needs serializer implicit Writes */
      Created(Json.toJson(doc)).withHeaders(jsonHeader, "Location" -> s"\\document_templates\\${doc._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[DocumentTemplate]) = Action {
    val docs: List[DocumentTemplate] = find(collectionName = "document_templates")
    Ok(Json.toJson(docs)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "document_templates", id)
      .map(doc => Ok(Json.toJson(doc)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

  def edit(id: String)(implicit manifest: Manifest[DocumentTemplate]) = Action(parse.json) { request =>
    request.body.validate[DocumentTemplate].map(doc =>
      update(collectionName = "document_templates", id, doc)
        .map(doc => Ok(Json.toJson(doc)).withHeaders(jsonHeader))
        .getOrElse {
        insert(collectionName = "document_templates", doc)
        Created(Json.toJson(doc)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete(collectionName = "document_templates", id)
      .map(doc => Ok(Json.toJson(doc)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }
}
