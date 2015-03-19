package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
case class CourseMeeting(
                           _id: String,                // todo date, how to make it unique ? todo add dateFormat
                           place: String,
                           subject: String,
                           description: Option[String],
                           meetingType: Option[String], // todo add Enums
                           meetingStatus: String,       // todo add Enums

                           presentMembers_ids: List[String]
                         )

case class Course(
                   _id: String,
                   name: String,
                   courseType: String,            // todo add Enums
                   meetingDates: List[String],    // you can get startDate and closeDate // todo add dateFormat
                                                  // you can get date for specific meeting
                   places: List[String],
                   description: String, 
                   meetingHistory: List[CourseMeeting],
                   
                   members_ids: List[String],            // you can get students number
                   graduatedMembers_ids: List[String],
                   instructor_id: String,
                   manager_id: String
                 )

object CourseMeeting {
  implicit val courseMeetingReads: Reads[CourseMeeting] = Json.reads[CourseMeeting]
  implicit val courseMeetingWrites: Writes[CourseMeeting] = Json.writes[CourseMeeting]
}

object Course {
  implicit val courseReads: Reads[Course] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "name").read[String] ~
    (__ \ "courseType").read[String] ~
    (__ \ "meetingDates").read[List[String]] ~
    (__ \ "places").read[List[String]] ~
    (__ \ "description").read[String] ~
    (__ \ "meetingHistory").read[List[CourseMeeting]] ~
    (__ \ "members_ids").read[List[String]] ~
    (__ \ "graduatedMembers_ids").read[List[String]] ~
    (__ \ "instructor_id").read[String] ~
    (__ \ "manager_id").read[String]
  )(Course.apply _)
  implicit val courseWrites: Writes[Course] = Json.writes[Course]
}
