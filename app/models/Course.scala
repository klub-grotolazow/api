package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
case class CourseMeeting(
                           _id: String,                // todo date, how to make it unique ? todo add dateFormat
                           group: Int,
                           place: String,
                           date: String,
                           subject: String,
                           description: Option[String],
                           meetingStatus: String,       // todo add Enums
                           instructor_id: String,
                           presentMembers_ids: List[String]
                         )

case class Course(
                   _id: String,
                   name: String,
                   meetingHistory: List[CourseMeeting],
                   members_ids: List[String],            // you can get students number
                   graduatedMembers_ids: List[String],
                   instructors_ids: List[String],
                   manager_id: String
                 )

object CourseMeeting {
  implicit val courseMeetingReads: Reads[CourseMeeting] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "group").read[Int] ~
    (__ \ "place").read[String] ~
    (__ \ "date").read[String] ~
    (__ \ "subject").read[String] ~
    (__ \ "description").readNullable[String] ~
    (__ \ "meetingStatus").read[String] ~
    (__ \ "instructor_id").read[String] ~
    (__ \ "presentMembers_ids").read[List[String]]
  )(CourseMeeting.apply _)
  implicit val courseMeetingWrites: Writes[CourseMeeting] = Json.writes[CourseMeeting]
}

object Course {
  implicit val courseReads: Reads[Course] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "name").read[String] ~
    (__ \ "meetingHistory").read[List[CourseMeeting]] ~
    (__ \ "members_ids").read[List[String]] ~
    (__ \ "graduatedMembers_ids").read[List[String]] ~
    (__ \ "instructors_ids").read[List[String]] ~
    (__ \ "manager_id").read[String]
  )(Course.apply _)
  implicit val courseWrites: Writes[Course] = Json.writes[Course]
}
