package controllers

import models._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.BodyParsers.parse
import play.api.mvc._
import utils.{MongoCollection, MongoDatabase}

class CourseCtrl extends Controller with MongoDatabase[Course] {

  val jsonHeader = ("Content-Type", "application/json")

  def create() = Action(parse.json) { request =>
    request.body.validate[Course].map { course =>
      insert("courses", course)
      Created(Json.toJson(course)).withHeaders(jsonHeader, "Location" -> s"\\courses\\${course._id}")
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getList = Action {
    val courses: List[Course] = find("courses")
    Ok(Json.toJson(courses)).withHeaders(jsonHeader)
  }

  def getOne(id: String) = Action {
    findOne("courses", id).map(course => 
      Ok(Json.toJson(course)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def edit(id: String) = Action(parse.json) { request =>
    request.body.validate[Course].map(course =>
      update("courses", id, course).map(course => 
        Ok(Json.toJson(course)).withHeaders(jsonHeader)
      ).getOrElse {
        insert("courses", course)
        Created(Json.toJson(course)).withHeaders(jsonHeader)
      }
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def remove(id: String) = Action {
    delete("courses", id).map(course => 
      Ok(Json.toJson(course)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def createMeeting(courseId: String) = Action(parse.json) { request =>
    request.body.validate[CourseMeeting].map(meeting =>
      findOne("courses", courseId).map { course =>
        update("courses", courseId, course.copy(meetingHistory = course.meetingHistory :+ meeting)).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      }.getOrElse(NotFound)
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def getMeetingsList(courseId: String) = Action {
    findOne("courses", courseId).map(course =>
      Ok(Json.toJson(course.meetingHistory)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def getOneMeeting(courseId: String, meetingId: String) = Action {
    findOne("courses", courseId).map { course =>
      val meeting = course.meetingHistory.filter(meeting => meeting._id == meetingId)
      if (meeting.isEmpty) NotFound
      else Ok(Json.toJson(meeting.head)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }
  
  def editMeeting(courseId: String, meetingId: String) = Action(parse.json) { request =>
    request.body.validate[CourseMeeting].map ( editedMeeting =>
      findOne("courses", courseId).map { course =>
        val editedCourseMeeting: List[CourseMeeting] = course.meetingHistory.map { meeting =>
          if (meeting._id == meetingId) editedMeeting
          else meeting
        }
        update ("courses", courseId, course.copy(meetingHistory = editedCourseMeeting) ).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      }.getOrElse(NotFound)
    ).recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def removeMeeting(courseId: String, meetingId: String) = Action {
    findOne("courses", courseId).map ( course =>
      update("courses", courseId, course.copy(meetingHistory = course.meetingHistory.filter(meeting => meeting._id != meetingId))).map(course =>
        Ok(Json.toJson(course)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    ).getOrElse(NotFound)
  }

  def getMeetingMembers(courseId: String, meetingId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      val meeting: List[CourseMeeting] = course.meetingHistory.filter(meeting => meeting._id == meetingId)
      if (meeting.isEmpty) NotFound
      else {
        val presentMembers: List[User] = new MongoCollection[User].find("users").filter(member =>
          meeting.head.presentMembers_ids contains member._id)
        if (presentMembers.isEmpty) PartialContent
        else Ok(Json.toJson(presentMembers)).withHeaders(jsonHeader)
      }
    }.getOrElse(NotFound)
  }

  // todo add special cases PartialContent etc
  def addMeetingMember(courseId: String, meetingId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      update("courses", courseId, course.copy(meetingHistory = course.meetingHistory.map {
        case meeting =>
          if (meeting._id == meetingId && !(meeting.presentMembers_ids contains memberId)) {
            meeting.copy(presentMembers_ids = meeting.presentMembers_ids :+ memberId)
          } else meeting
      })).map(course =>
        Ok(Json.toJson(course)).withHeaders(jsonHeader)
      ).getOrElse(NotFound)
    }.getOrElse(NotFound)
  }
  
  def removeMeetingMember(courseId: String, meetingId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      update("courses", courseId, course.copy(meetingHistory = course.meetingHistory.map {
        case meeting =>
          if (meeting._id == meetingId && (meeting.presentMembers_ids contains memberId)) {
            meeting.copy(presentMembers_ids = meeting.presentMembers_ids diff List(memberId))
          } else meeting
      })).map(course =>
        Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
    }.getOrElse(NotFound)
  }

  def getMembersList(courseId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      val members: List[User] = new MongoCollection[User].find("users").filter(member =>
        course.members_ids contains member._id)
      if (members.isEmpty) NotFound
      else Ok(Json.toJson(members)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }
  
  def addMember(courseId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!(course.members_ids contains memberId)) {
        update("courses", courseId, course.copy(members_ids = course.members_ids :+ memberId)).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }
  
  def removeMember(courseId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (course.members_ids contains memberId) {
        update("courses", courseId, course.copy(members_ids = course.members_ids diff List(memberId))).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)  
  }

  def getGraduatedMembersList(courseId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      val graduatedMembers: List[User] = new MongoCollection[User].find("users").filter(member =>
        course.graduatedMembers_ids contains member._id)
      if (graduatedMembers.isEmpty) NotFound
      else Ok(Json.toJson(graduatedMembers)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)  
  }
  
  def addGraduatedMember(courseId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!(course.graduatedMembers_ids contains memberId)) {
        update("courses", courseId, course.copy(graduatedMembers_ids = course.graduatedMembers_ids :+ memberId)).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)  
  }
  
  def removeGraduatedMember(courseId: String, memberId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (course.graduatedMembers_ids contains memberId) {
        update("courses", courseId, course.copy(graduatedMembers_ids = course.graduatedMembers_ids diff List(memberId))).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)    
  }

  def getInstructor(courseId: String, instructorId: String) = Action {
    new MongoCollection[User].findOne("users", instructorId).map(instructor =>
      Ok(Json.toJson(instructor)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def changeInstructor(courseId: String, instructorId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!(course.instructor_id equals instructorId)) {
        update("courses", courseId, course.copy(instructor_id = instructorId)).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }
  
  def removeInstructor(courseId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!course.instructor_id.isEmpty) {
        update("courses", courseId, course.copy(instructor_id = "")).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }

  def getManager(courseId: String, managerId: String) = Action {
    new MongoCollection[User].findOne("users", managerId).map(instructor =>
      Ok(Json.toJson(instructor)).withHeaders(jsonHeader)
    ).getOrElse(NotFound)
  }

  def changeManager(courseId: String, managerId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!(course.manager_id equals managerId)) {
        update("courses", courseId, course.copy(manager_id = managerId)).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }

  def removeManager(courseId: String) = Action {
    findOne("courses", courseId).map { course: Course =>
      if (!course.manager_id.isEmpty) {
        update("courses", courseId, course.copy(manager_id = "")).map(course =>
          Ok(Json.toJson(course)).withHeaders(jsonHeader)
        ).getOrElse(NotFound)
      } else Ok(Json.toJson(course)).withHeaders(jsonHeader)
    }.getOrElse(NotFound)
  }
}