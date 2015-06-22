package controllers

import models._
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import utils.{AuthorizationAction, MongoCollection, MongoDatabase}

class UserCtrl(action: AuthorizationAction) extends Controller with MongoDatabase[User] {

  val jsonHeader = ("Content-Type", "application/json")
  

  def getCurrent() = action.authorize(parse.empty) { user => request =>

    // todo replace by Cache.getAs[String]("id").get
    val userName: String = request.headers.get("Authorization").map { token =>
      token split "&" match {
        case Array(userName: String, authToken: String) => userName
        case _ => ""
      }
    }.getOrElse("")
    findOneByCondition("users", "auth.userName" -> userName ).map(user =>
      Ok(Json.toJson(user)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def editCurrent() = action.authorize(parse.json) { user => request =>
    request.body.validate[User].map{user =>

      // todo replace by Cache.getAs[String]("id").get
      val userName: String = request.headers.get("Authorization").map { token =>
        token split "&" match {
          case Array(userName: String, authToken: String) => userName
          case _ => ""
        }
      }.getOrElse("")
      val currentId = findOneByCondition("users", "auth.userName" -> userName).get._id

      update("users", currentId, user).map(user =>
        Ok(Json.toJson(user)).withHeaders(jsonHeader)
      ).getOrElse {
        insert("users", user)
        Created(Json.toJson(user)).withHeaders(jsonHeader)
      }
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
  
  def create() = action.authorize(parse.json) { user => request =>
    /** needs deserializer implicit Reads */
    request.body.validate[User].map { user =>
        insert("users", user)
        /** needs serializer implicit Writes */
        Created(Json.toJson(user)).withHeaders(jsonHeader, "Location" -> s"\\users\\${user._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList = action.authorize(parse.empty) { user => request =>
    val users: List[User] = find("users")
    Ok(Json.toJson(users)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = action.authorize(parse.empty) { user => request =>
    findOne("users", id).map(user => 
      Ok(Json.toJson(user)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }  

  def edit(id: String) = action.authorize(parse.json) { user => request =>
    request.body.validate[User].map(user =>
      update("users", id, user).map(user => 
        Ok(Json.toJson(user)).withHeaders(jsonHeader)
      ).getOrElse {
          insert("users", user)
          Created(Json.toJson(user)).withHeaders(jsonHeader)
        }
      ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
  
  def remove(id: String) = action.authorize(parse.empty) { user => request =>
    delete("users", id).map(user => 
      Ok(Json.toJson(user)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }
  
  def getCoursesList(userId: String) = action.authorize(parse.empty) { user => request =>
    try{
      findOne("users", userId).map(user => {
        val courses: List[Course] = user.currentCourses_ids.map(course_id =>
          new MongoCollection[Course].findOne("courses", course_id).getOrElse(throw new DocumentNotFoundException)
        )
        Ok(Json.toJson(courses)).withHeaders(jsonHeader)
      }).getOrElse(NotFound)
    } catch {
      case e: DocumentNotFoundException => PartialContent
    }
  }

  def getEquipmentsList(userId: String) = action.authorize(parse.empty) { user => request =>
    try {
      findOne("users", userId).map(user => {
        val equipments: List[Equipment] = user.hiredEquipments_ids.map(equipment_id =>
          new MongoCollection[Equipment].findOne("equipments", equipment_id).getOrElse(throw new DocumentNotFoundException)
        )
        Ok(Json.toJson(equipments)).withHeaders(jsonHeader)
      }).getOrElse(NotFound)
    } catch {
      case e: DocumentNotFoundException => PartialContent
    }
  }

  def getPaymentsList(userId: String) = action.authorize(parse.empty) { user => request =>
    try {
      findOne("users", userId).map(user => {
        val payments: List[Payment] = user.payments_ids.map(payment_id =>
          new MongoCollection[Payment].findOne("payments", payment_id).getOrElse(throw new DocumentNotFoundException)
        )
        Ok(Json.toJson(payments)).withHeaders(jsonHeader)
      }).getOrElse(NotFound)
    } catch {
      case e: DocumentNotFoundException => PartialContent
    }
  }
}
