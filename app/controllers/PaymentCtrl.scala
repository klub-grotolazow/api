package controllers

import models.Payment
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import utils.MongoDatabase

class PaymentCtrl extends Controller with MongoDatabase[Payment] {

  val jsonHeader = ("Content-Type", "application/json")

  /** needs BodyParser BodyParsers.parse.asJson */
  def create()(implicit manifest: Manifest[Payment]) = Action(parse.json) { request =>
    /** needs deserializer implicit Reads */
    request.body.validate[Payment].map { payment =>
      insert(collectionName = "payments", payment)
      /** needs serializer implicit Writes */
      Created(Json.toJson(payment)).withHeaders(jsonHeader, "Location" -> s"\\payments\\${payment._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList()(implicit manifest: Manifest[Payment]) = Action {
    val payments: List[Payment] = find(collectionName = "payments")
    Ok(Json.toJson(payments)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne(collectionName = "payments", id)
      .map(payment => Ok(Json.toJson(payment)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

  def edit(id: String)(implicit manifest: Manifest[Payment]) = Action(parse.json) { request =>
    request.body.validate[Payment].map(payment =>
      update(collectionName = "payments", id, payment)
        .map(payment => Ok(Json.toJson(payment)).withHeaders(jsonHeader))
        .getOrElse {
        insert(collectionName = "payments", payment)
        Created(Json.toJson(payment)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete(collectionName = "payments", id)
      .map(payment => Ok(Json.toJson(payment)).withHeaders(jsonHeader))
      .getOrElse(NotFound)
  }

}
