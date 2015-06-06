package controllers

import models.{Course, User, Payment}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import utils.{AuthorizationAction, MongoCollection, MongoDatabase}

class PaymentCtrl(action: AuthorizationAction) extends Controller with MongoDatabase[Payment] {

  val jsonHeader = ("Content-Type", "application/json")

  def create() = action.authorize(parse.json) { user => request =>
    request.body.validate[Payment].map { payment =>
      insert("payments", payment)
      Created(Json.toJson(payment)).withHeaders(jsonHeader, "Location" -> s"\\payments\\${payment._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList = action.authorize(parse.empty) { user => request =>
    val payments: List[Payment] = find("payments")
    Ok(Json.toJson(payments)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = action.authorize(parse.empty) { user => request =>
    findOne("payments", id).map(payment => 
      Ok(Json.toJson(payment)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def edit(id: String) = action.authorize(parse.json) { user => request =>
    request.body.validate[Payment].map(payment =>
      update("payments", id, payment).map(payment => 
        Ok(Json.toJson(payment)).withHeaders(jsonHeader)
      ).getOrElse {
        insert("payments", payment)
        Created(Json.toJson(payment)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = action.authorize(parse.empty) { user => request =>
    delete("payments", id).map(payment => 
      Ok(Json.toJson(payment)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def getUser(paymentId: String) = action.authorize(parse.empty) { user => request =>
    findOne("payments", paymentId).map(payment =>
      new MongoCollection[User].findOne("users", payment.user_id).map(user =>
        Ok(Json.toJson(user)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    ).getOrElse(NotFound)
  }
  
  def getCourse(paymentId: String) = action.authorize(parse.empty) { user => request =>
      findOne("payments", paymentId).map(payment =>
        new MongoCollection[Course].findOne("courses", payment.course_id).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      ).getOrElse(NotFound)
  }
  
  def getAccounter(paymentId: String) = action.authorize(parse.empty) { user => request =>
      findOne("payments", paymentId).map(payment =>
        new MongoCollection[User].findOne("users", payment.accounter_id).map(accounter =>
          Ok(Json.toJson(accounter)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      ).getOrElse(NotFound)
  }
}
