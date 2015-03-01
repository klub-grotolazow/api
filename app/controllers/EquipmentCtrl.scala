package controllers

import models.Equipment
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import utils.MongoDatabase

class EquipmentCtrl extends Controller with MongoDatabase[Equipment] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[Equipment]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[Equipment].map { equipment =>
      insert(collectionName = "equipments", equipment)
      /** needs serializer implicit Writes */
      Created(Json.toJson(equipment)).withHeaders(jsonHeader, "Location" -> s"\\equipments\\${equipment._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[Equipment]) = Action {
    val equipments: List[Equipment] = find(collectionName = "equipments")
    Ok(Json.toJson(equipments)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "equipments", id)
      .map(equipment => Ok(Json.toJson(equipment)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

  def edit(id: String)(implicit manifest: Manifest[Equipment]) = Action(parse.json) { request =>
    request.body.validate[Equipment].map(equipment =>
      update(collectionName = "equipments", id, equipment)
        .map(equipment => Ok(Json.toJson(equipment)).withHeaders(jsonHeader))
        .getOrElse {
        insert(collectionName = "equipments", equipment)
        Created(Json.toJson(equipment)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete(collectionName = "equipments", id)
      .map(equipment => Ok(Json.toJson(equipment)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

}
