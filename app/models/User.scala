package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Address(
                    voivodeship: Option[String],
                    town: String,
                    street: String,
                    buildingNr: Int,
                    apartmentNr: Option[Int],
                    zipCode: Option[String]   // todo add Regex
                    )

case class User(
                 _id: String,
                 firstName: String,
                 lastName: String,
                 email: String,
                 peselNr: Option[String],          // todo add Regex
                 idCardNr: Option[String],         // todo add Regex
                 feeStatus: String,                // todo add Enum
                 hoursPoints: Int,
                 address: Option[Address],
                 age: Option[Int],
                 phoneNr: Option[String],
                 ICEphoneNr: Option[String],
                 indexNr: Option[String],          // todo add Regex       // for student
                 instructorCard: Option[String],   // todo add Regex       // for instructor
                 currentCourses_ids: List[String],
                 hiredEquipments_ids: List[String],
                 payments_ids: List[String]
               )

object Address {
  implicit val addressReads: Reads[Address] = Json.reads[Address]
  implicit val addressWrites: Writes[Address] = Json.writes[Address]
}

object User {
  implicit val userReads: Reads[User] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "firstName").read[String] ~
    (__ \ "lastName").read[String] ~
    (__ \ "email").read(email) ~
    (__ \ "peselNr").readNullable[String] ~
    (__ \ "idCardNr").readNullable[String] ~
    (__ \ "feeStatus").read[String] ~
    (__ \ "hoursPoints").read[Int] ~
    (__ \ "address").readNullable[Address] ~
    (__ \ "age").readNullable[Int] ~
    (__ \ "phoneNr").readNullable[String] ~
    (__ \ "ICEphoneNr").readNullable[String] ~
    (__ \ "indexNr").readNullable[String] ~
    (__ \ "instructorCard").readNullable[String] ~
    (__ \ "currentCourses_ids").read[List[String]] ~
    (__ \ "hiredEquipments_ids").read[List[String]] ~
    (__ \ "payments_ids").read[List[String]]
    )(User.apply _)

  implicit val userWrites: Writes[User] = Json.writes[User]

  implicit val userFormat: Format[User] =
  Format(userReads, userWrites)
}