package controllers

import models.{User, EquipmentHire, Equipment}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import utils.{MongoCollection, MongoDatabase}

class EquipmentCtrl extends Controller with MongoDatabase[Equipment] {

  val jsonHeader = ("Content-Type", "application/json")

  def create() = Action(parse.json) { request =>
    request.body.validate[Equipment].map { equipment =>
      insert("equipments", equipment)
      Created(Json.toJson(equipment)).withHeaders(jsonHeader, "Location" -> s"\\equipments\\${equipment._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList = Action {
    val equipments: List[Equipment] = find("equipments")
    Ok(Json.toJson(equipments)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne("equipments", id).map(equipment => 
      Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def edit(id: String) = Action(parse.json) { request =>
    request.body.validate[Equipment].map(equipment =>
      update("equipments", id, equipment).map(equipment => 
        Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
      ).getOrElse {
        insert("equipments", equipment)
        Created(Json.toJson(equipment)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete("equipments", id).map(equipment => 
      Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def hire(equipmentId: String) = Action(parse.json) { request =>
    request.body.validate[EquipmentHire].map(hire => {
      findOne("equipments", equipmentId).map { equipment =>
        update("equipments", equipmentId, equipment.copy(hireHistory = equipment.hireHistory :+ hire)).map(equipment =>
          Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      }.getOrElse(NotFound)
    }).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def listHires(equipmentId: String) = Action {
    findOne("equipments", equipmentId).map(equipment => 
      Ok(Json.toJson(equipment.hireHistory)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def getOneHire(equipmentId: String, hireId: String) = Action {
      findOne("equipments", equipmentId).map { equipment =>
        val hire = equipment.hireHistory.filter(hire => hire._id == hireId)
        if (hire.isEmpty) NotFound
        else Ok(Json.toJson(hire.head)).withHeaders(jsonHeader)
      }.getOrElse(NotFound)
  }
  
  def editHire(equipmentId: String, hireId: String) = Action(parse.json) { request =>
    request.body.validate[EquipmentHire].map ( editedHire =>
      findOne("equipments", equipmentId).map { equipment =>
        val editedEquipmentHire: List[EquipmentHire] = equipment.hireHistory.map { hire =>
          if (hire._id == hireId) editedHire
          else hire
        }
        update ("equipments", equipmentId, equipment.copy(hireHistory = editedEquipmentHire) ).map(equipment => 
          Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      }.getOrElse(NotFound)
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def removeHire(equipmentId: String, hireId: String) = Action {
    findOne("equipments", equipmentId).map ( equipment =>
      update("equipments", equipmentId, equipment.copy(hireHistory = equipment.hireHistory.filter(hire => hire._id != hireId))).map(equipment => 
        Ok(Json.toJson(equipment)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    ).getOrElse(NotFound)
  }

  def getHireUser(equipmentId: String, hireId: String) = Action {
    findOne("equipments", equipmentId).map { equipment =>
      val hire = equipment.hireHistory.filter(hire => hire._id == hireId)
      if (hire.isEmpty) NotFound
      else new MongoCollection[User].findOne("users", hire.head.user_id).map(user =>
        Ok(Json.toJson(user)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    }.getOrElse(NotFound)
  }

  def getHireWarehouseman(equipmentId: String, hireId: String) = Action {
    findOne("equipments", equipmentId).map { equipment =>
      val hire = equipment.hireHistory.filter(hire => hire._id == hireId)
      if (hire.isEmpty) NotFound
      else new MongoCollection[User].findOne("users", hire.head.warehouseman_id).map(user =>
        Ok(Json.toJson(user)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    }.getOrElse(NotFound)
  }

}
